package net.dalesalter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

public class ChatPeer {

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";

    static class ServerThread extends Thread {

        /**
         * User may specify a port to have a socket bind to, default is 8888 if none specified
         */
        private static int inputPort = 0;

        public ServerThread(String args[]){
            if(args.length == 2) {
                try {
                    // Turns the string argument into an integer (one we can actually use to define the port)
                    inputPort = Integer.parseInt(args[0]);

                    // Checks to see if the port number sent is actually within the valid port ranges
                    if (inputPort >= 65536 || inputPort < 0){
                        System.err.print("Port: " + inputPort + " is out of the valid port ranges 0 - 65535");

                        // In an unrecoverable state, we must exit the application
                        System.exit(1);
                    }
                } catch (NumberFormatException e){
                    // The value sent is not able to be turned into a port,  therefore we must exit
                    System.err.println("Argument" + args[0] + " must be an interger.");

                    // In an unrecoverable state, we must exit the application
                    System.exit(1);
                }

                try {
                    validPeers = Config.load(args[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Usage: <Server Port> <Configuration File>");
                System.exit(1);
            }


            System.out.println("------------------------ Imported valid peers -------------------------");
            for ( HashMap<Integer, Peer> portPeerSet: validPeers.values() )
                for ( Peer peer : portPeerSet.values() )
                    System.out.println(peer);
            System.out.println("-----------------------------------------------------------------------");

            System.out.println("Server -> Configured to listen on port " + inputPort);
        }

        public void run(){

            try {
                // Creates a UDP socket, which will listen to all the UDP messages sent to it on the previously given port
                duplexSocket = new DatagramSocket(inputPort);
            } catch (SocketException e) {
                System.err.println("Critical error, must terminate. Do you have another server bound to this port? - "
                        + inputPort);
                // In an unrecoverable state, we must exit the application
                System.exit(1);
            }

            System.out.println("Server -> Listening for messages at: " + duplexSocket);

            // The IP address and port number of the person who sent the UDP packet, need to know when sending a response back
            InetAddress IPAddress = null;
            int port = 0;

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = null;
            String receivedMessageString = "";

            while(true) {
                try{
                    receivePacket = new DatagramPacket(receiveData, receiveData.length);

                    duplexSocket.receive(receivePacket);

                    IPAddress = receivePacket.getAddress();
                    port = receivePacket.getPort();

                    // Checks to see if they exist
                    if(validPeers.containsKey(IPAddress) && validPeers.get(IPAddress).containsKey(port)){
                        receivedMessageString = new String(receivePacket.getData());

                        System.out.println("\nServer Status -> " + validPeers.get(IPAddress).get(port) + " -> " + ANSI_BLUE + receivedMessageString.trim() + ANSI_RESET);

                    }
                    else  {
                        if(!unauthorisedPeers.contains(IPAddress)){
                            System.out.println("Server -> Unauthorized chat request from <" + IPAddress + ">");
                            unauthorisedPeers.add(IPAddress);
                        }
                    }

                    receiveData = new byte[1024];
                    System.out.print("Client status -> Send Message ~> ");
                }
                catch (IOException e)
                {
                    System.out.println("Something has gone wrong with - " + IPAddress + ":" + port);
                }
            }
        }

    }

    static class ClientThread extends Thread {

        public ClientThread(){}

        public void run(){

            DatagramPacket sendPacket;
            byte[] sendData = new byte[1024];

            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

            String sentence = "";

            while(true) {
                try {

                    System.out.print("Client status -> Send Message ~> ");

                    try {
                        sentence = inFromUser.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    sentence += "\r\n";
                    sendData = sentence.getBytes();

                    for (HashMap<Integer, Peer> portPeerSet : validPeers.values())
                        for (Peer peer : portPeerSet.values()) {
                            sendPacket = new DatagramPacket(sendData, sendData.length, peer.getPeerIP(), peer.getPeerPort());
                            duplexSocket.send(sendPacket);
                        }

                }
                catch (IOException e)
                {
                    System.out.println("Something has gone wrong sending the packet to the peers!");
                }
            }
        }

    }





    // IP -> Port -> Peer
    public static HashMap<InetAddress, HashMap<Integer, Peer>> validPeers = new HashMap<InetAddress, HashMap<Integer, Peer>>();

    // IP -> Peer
    public static HashSet<InetAddress> unauthorisedPeers = new HashSet<InetAddress>();

    private static DatagramSocket duplexSocket = null;

    public static void main(String[] args) {
        ServerThread st = new ServerThread(args);
        ClientThread ct = new ClientThread();

	    // write your code here
        // ServerThread a = new ServerThread();
        // ClientThread b = new ClientThread();

        // a.start();
        // b.start();


        st.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ct.start();
    }
}
