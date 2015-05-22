package net.dalesalter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

/**
 * PeerToPeer
 *
 * Used to connect and chat to together people using the same PeerToPeer client
 *  Once you set up the configuration file, you can then just chat away
 *
 * This program support authentication, if people are not in the configuration file
 *  you are warned that they are trying to connect and then nothing more is displayed
 *
 * This program has been put together and inspired from various places
 *  [1] - https://docs.oracle.com/javase/tutorial/essential/environment/cmdLineArgs.html
 *  [2] - http://docs.oracle.com/javase/1.5.0/docs/api/java/net/InetAddress.html#getByName%28java.lang.String%29
 *  [3] - http://www.rgagnon.com/javadetails/java-check-if-a-filename-is-valid.html
 */
public class ChatPeer {
    /***
     * ServerThread is concerned with waiting for
     * packets sent from other peers and then displaying
     * them to the terminal
     */
    static class ServerThread extends Thread {
        public ServerThread(){}

        /**
         *  Is the method that is invoked when the thread is started with start()
         */
        public void run(){
            InetAddress IPAddress = null;
            int port = 0;

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = null;

            String receivedMessageString = "";

            // Used to store IP address that have already printed to the application
            //  about them being unauthorized
            HashSet<InetAddress> unauthorisedPeers = new HashSet<InetAddress>();

            while(true) {
                try{
                    // Creates a packet object read to be filled by other peers sending in their chat messages
                    receivePacket = new DatagramPacket(receiveData, receiveData.length);

                    // Blocks until a chat message has been sent by any peers
                    fullDuplexUDPSocket.receive(receivePacket);

                    // We will need to get the IP address and Port to check whether it is an authorized peer
                    IPAddress = receivePacket.getAddress();
                    port = receivePacket.getPort();

                    // Checks to see if they are authorized given a IP and Port
                    if(validPeers.containsKey(IPAddress) && validPeers.get(IPAddress).containsKey(port)){
                        // Turns the pack information into a string
                        receivedMessageString = new String(receivePacket.getData());

                        Peer sendingPeer = validPeers.get(IPAddress).get(port);

                        // Prints the pretty coloured message to the terminal
                        System.out.println("\nServer Status -> " + sendingPeer.getPeerName() + " <" + sendingPeer.getPeerIP() + "> " + receivedMessageString.trim());
                    }

                    // If they are not authorized
                    else  {
                        // If the user has not been warned before of the IP being unauthorized, warn them
                        if(!unauthorisedPeers.contains(IPAddress)) {
                            System.out.println("Server -> Unauthorized chat request from <" + IPAddress + ">");

                            // Add the IP to the warning list so it does not get triggered next time
                            unauthorisedPeers.add(IPAddress);

                            ClientThread.UserInputPrompt();
                        }
                        continue;
                    }

                    // Reset the byte array, avoid printing problems
                    receiveData = new byte[1024];

                    // Corrects the terminal output still look consistent after an asynchronous message has been sent
                    //  from a peer

                    ClientThread.UserInputPrompt();
                }
                catch (IOException e)
                {
                    // We should continue on because the error is not critical
                    System.out.println("Something has gone wrong with " + IPAddress + ":" + port);
                }
            }
        }

    }

    /**
     * ClientThread is concerned with taking user input
     *  and then sending it to all of the trusted peers
     */
    static class ClientThread extends Thread {
        public ClientThread(){}

        /**
         *  Is the method that is invoked when the thread is started with start()
         */
        public void run(){
            // Buffers user input
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

            // Required information to send the packet to other peers
            DatagramPacket sendPacket;
            byte[] sendData = new byte[1024];
            String sentence = "";

            while(true) {
                try {

                    UserInputPrompt();

                    try {
                        sentence = inFromUser.readLine(); // Blocks here until a user enters their message
                    } catch (IOException e) {
                        System.err.println("Commandline input error, try again?");
                        continue; // Attempts to take in more input
                    }

                    sentence += "\r\n"; // Adds a new line to the message
                    sendData = sentence.getBytes(); // Turns the string into bytes for the Datagram packet

                    // Loops over all IP addresses and then all Ports those IP addresses may have clients on
                    for (HashMap<Integer, Peer> portPeerSet : validPeers.values())
                        for (Peer peer : portPeerSet.values()) {

                            // Creates the packet with the input and then sends it off to the clients
                            sendPacket = new DatagramPacket(sendData, sendData.length, peer.getPeerIP(), peer.getPeerPort());
                            fullDuplexUDPSocket.send(sendPacket);
                        }

                }
                catch (IOException e)
                {
                    System.out.println("Something has gone wrong sending the packet to the peers, continuing");
                }
            }
        }

        /**
         * Prints the prompt, may need to be used by the server so the output seems correct
         */
        public static void UserInputPrompt(){
            System.out.println("Client status -> Send Message");
            System.out.print(" ~> ");
        }


    }

    /**
     * setUp sets to entire project up with the given arguments
     *  - Loading configuration files
     *  - Setting up validPeers
     *  - Setting up the DatagramSocket
     * @param args The command line arguments given to the application
     */
    public static void setUp(String[] args){
        int serverPort = 0;

        if(args.length == 2) {
            try {
                // Turns the string argument into an integer (one we can actually use to define the port)
                serverPort = Integer.parseInt(args[0]);

                // Checks to see if the port number sent is actually within the valid port ranges
                if (serverPort >= 65536 || serverPort < 0){
                    System.err.print("Port: " + serverPort + " is out of the valid port ranges 0 - 65535");

                    // In an unrecoverable state, we must exit the application
                    System.exit(1);
                }
            } catch (NumberFormatException e){
                // The value sent is not able to be turned into a port,  therefore we must exit
                System.err.println("Argument" + args[0] + " must be an interger.");

                // In an unrecoverable state, we must exit the application
                System.exit(1);
            }

            // Attempts to load in from the given configuration file, all of the authorized peers
            try {
                validPeers = Config.load(args[1]);
            } catch (IOException e) {
                System.err.println("Unable to load in configuration file " + args[1] + " check that it is available");
                System.exit(1);
            }

        } else {
            System.out.println("Usage: <Server Port> <Configuration File>");
            System.exit(1);
        }


        // Prints all the peers that were loaded in from the configuration file
        System.out.println("------------------------ Imported valid peers -------------------------");
        for ( HashMap<Integer, Peer> portPeerSet: validPeers.values() )
            for ( Peer peer : portPeerSet.values() )
                System.out.println(peer);
        System.out.println("-----------------------------------------------------------------------");

        // Sets up the full duplex socket (communicate to and from using the same socket)
        try {
            fullDuplexUDPSocket = new DatagramSocket(serverPort);
        } catch (SocketException e) {
            System.err.println("Critical error, must terminate. Do you have another server bound to this port? - "
                    + serverPort);
            // In an unrecoverable state, we must exit the application
            System.exit(1);
        }

    }


    /**
     * This data structure stores all the the valid peers loaded in from the configuration file
     *  allows you to have multiple peers communicate from the same IP address by using ports
     *  & the IP as identifiers
     *
     * IP -> Port -> Peer
     */
    public static HashMap<InetAddress, HashMap<Integer, Peer>> validPeers = new HashMap<InetAddress, HashMap<Integer, Peer>>();

    /**
     * The communication socket, other peers will send messages to this socket
     * The client will also send messages to other peers using this socket
     */
    private static DatagramSocket fullDuplexUDPSocket = null;

    /**
     * Application starting point
     * @param args Arguments passed in from the command line when starting the application
     */
    public static void main(String[] args) {
        setUp(args);

        // Sets up the server thread
        ServerThread st = new ServerThread();

        // Sets up the client thread
        ClientThread ct = new ClientThread();


        // Starts the server
        st.start();

        // Delays the client so that the command line input should be in the right order
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Starts the client, this stores the RPEL and the System.in will stay here
        ct.start();
    }
}

