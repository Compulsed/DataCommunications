package net.dalesalter;

import java.net.*;
import java.io.*;

/**
 * Hello Server - UDP (Server)
 * This program is used to create a UDP server on a specified port
 * Any UDP packets will be responsed by a hard coded message printing my name and student ID.
 *
 * Created by Dale Salter (9724 397) on 29/04/2015.
 *
 * This program has been put together and inspired from various places
 *  [1] - https://docs.oracle.com/javase/tutorial/essential/environment/cmdLineArgs.html
 *  [2] - http://www.cs.uic.edu/~troy/spring05/cs450/sockets/UDPServer.java
 */
public class Server {

    /**
     * Defines a variable of which will later be constructed into the socket which will listen to UDP
     *  packets sent from various clients
     */
    private static DatagramSocket serverSocket = null;


    /**
     * Defines a variable that will be referenced later on for the acceptance of the packet
     *  to that was sent to the server by the client
     */
    private static DatagramPacket receivePacket = null;

    /**
     * Defines a byte array to store the contents of the UDP packet sent from the client
     *  the max size of this array will be 1024KB
     */
    private static byte[] receiveData = new byte[1024];


    /**
     * Defines a variable that will be referenced later on for the construction on the packet
     *  to send back to the client
     */
    private static DatagramPacket sendPacket = null;

    /**
     * Defines a byte array to store the contents of the UDP packet for the response to the client
     *  the max size of this array will be 1024KB
     */
    private static byte[] sendData = new byte[1024];

    /**
     * User may specify a port to have socket bind to, default is 8888 if none is specified
     */
    private static int inputPort = 8888;

    /**
     * Custom response back to the sender, this will be put on every UDP packet
     */

    private static String response = "Hello, my name is Dale Salter and my ID is 9724 397";


    /**
     * [2] - Sets up the socket with the previously specified port
     */
    private static void setUpServer()
    {
        try {
            // Creates a UDP socket, which will listen to all the UDP messages sent to it on the previously given port
            serverSocket = new DatagramSocket(inputPort);
        } catch (SocketException e) {
            System.err.println("Critical error, must terminate. Do you have another server bound to this port? - "
                    + inputPort);
            // In an unrecoverable state, we must exit the application
            System.exit(1);
        }
    }

    /**
     * [2] - Starts running the server, listening on the previously set up socket
     */
    private static void runServer()
    {
        // The IP address and port number of the person who sent the UDP packet, need to know when sending a response back
        InetAddress IPAddress = null;
        int port = 0;


        while(true) {
            try {
                // Prepares for a packet to be sent to the server, the Datagram packet will need to know the length
                //  and the buffer to put the received bytes
                receivePacket = new DatagramPacket(receiveData, receiveData.length);

                System.out.println("Server Status -> Waiting for Datagram packet");

                // Blocks until there has been a UDP packet sent to the server
                //  unblocks when it has finished filling in receive packet with data from the client UDP data
                serverSocket.receive(receivePacket);

                // Extracts the port and the IP address of the client, will need to know for sending a response back
                IPAddress = receivePacket.getAddress();
                port = receivePacket.getPort();

                // Extracts the bytes out of the string ready to respond to the message
                sendData = response.getBytes();

                // Prepares the packet to be sent back as a response, includes all of the required details
                //  Custom message, IP, port, length of the custom message
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

                // Sends the created UDP packet to the client
                serverSocket.send(sendPacket);

                System.out.println("Server Status -> Sent packet containing custom message to IP: "
                        + IPAddress + " Port:" + port);
            }
            catch (IOException e)
            {
                // Something has gone wrong with processing the network packet, error not fatal
                System.out.println("Something has gone wrong with - " + IPAddress + ":" + port);
            }
        }

    }

    /**
     * The entry point to the server application
     * @param args First argument is the port number, if none specified 8888 will be used
     */
    public static void main(String[] args) {
        // [1] Assume if there are any arguments given to the application
        // it will be a given port number as the first argument
        if(args.length > 0) {
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
        }

        System.out.println("System Status -> Selected port to listen on is: " + inputPort);

        // Sets up the UDP port
        setUpServer();

        // Listens to incoming UDP packets on that port
        runServer();


        return;
    }

}
