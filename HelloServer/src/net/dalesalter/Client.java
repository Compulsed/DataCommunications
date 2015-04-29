package net.dalesalter;

import java.net.*;
import java.io.*;

/**
 * Hello Server - UDP (Client)
 *
 * Created by Dale Salter (9724 397) on 29/04/2015.
 * This program is used to connect to the Hello Server - UDP (Client) on the specified port
 * and IP address. It should return a message containing my name and student ID.
 *
 * This program has been put together and inspired from various places
 *  [1] - https://docs.oracle.com/javase/tutorial/essential/environment/cmdLineArgs.html
 *  [2] - http://www.cs.uic.edu/~troy/spring05/cs450/sockets/UDPClient.java
 *  [3] - http://docs.oracle.com/javase/1.5.0/docs/api/java/net/InetAddress.html#getByName%28java.lang.String%29
 */
public class Client {

    /**
     * The user specified IP address that they believe the server is listening on
     */
    private static InetAddress serverIP = null;

    /**
     * The user specified port number that they believe the server is on
     */
    private static int serverPort = 0;


    /**
     * The socket in which the request will be sent from
     */
    private static DatagramSocket clientSocket = null;

    /**
     * Defines a variable that will be referenced later on for the acceptance of the packet
     *  to that was sent to the client by the server
     */
    private static DatagramPacket receivePacket = null;

    /**
     * Defines a byte array to store the contents of the UDP packet sent from the server
     *  the max size of this array will be 1024KB
     */
    private static byte[] receiveData = new byte[1024];


    /**
     * Defines a variable that will be referenced later on for the construction on the packet
     *  to send to the server
     */
    private static DatagramPacket sendPacket = null;

    /**
     * Defines a byte array to store the contents of the UDP packet message to the server
     *  the max size of this array will be 1024KB
     */
    private static byte[] sendData = new byte[1024];

    /**
     * The entry point to the client application
      * @param args
     *      args[0] IP of the corresponding server in format "192.168.1.1"
     *      args[1] Port of the corresponding server in format "8888"
     */
    public static void main(String[] args) {
        // We must validate the commandline inputs into the application (IP address, Port number)
        // [1] - Arguments validation
        // [3] - Parsing the IP Address string to and INetAddress
        if(args.length == 2){
            try {
                // Checks that the IP given to the application is of the right form
               serverIP = InetAddress.getByName(args[0]);
            } catch (UnknownHostException e) {
                // The IP given is not of a valid format, therefore we must exit and let them try again
                System.err.println("The IP given '" + args[0] + "' is of an invalid format");
                System.exit(1);
            }

            try {
                // Turns the string argument into an integer (one we can actually use to define the port)
                serverPort = Integer.parseInt(args[1]);

                // Checks to see if the port number sent is actually within the valid port ranges
                if (serverPort >= 65536 || serverPort < 0){
                    System.err.print("Port: " + serverPort + " is out of the valid port ranges 0 - 65535");
                    System.exit(1);
                }
            } catch (NumberFormatException e){
                // The value sent is not able to be turned into a port,  therefore we must exit and let them try again
                System.err.println("Argument " + args[1] + " must be an interger.");
                System.exit(1);
            }
        }
        else {
            // Prints the response to the user so that they know how to use the application on the command line
            System.out.println("Usage: <IP of UDP Server> <Port of UDP server>");

            // We should exit and let them user try again
            System.exit(1);
        }

        // [2] - Sending a Datagram Packet to the server
        // Creates a socket with a random value that the message will be sent from
        //  if the socket cannot created an exception is thrown
        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            System.err.println("Output Datagram socket cannot be made, attempt to rerun the application");

            // In an unrecoverable state, we must exit the application
            System.exit(1);
        }


        // Attempts to send an empty packet to the Hello Server just for a response
        try {
            clientSocket.send(new DatagramPacket(sendData, sendData.length, serverIP, serverPort));
        } catch (IOException e) {
            // Unable to send the UDP packet
            e.printStackTrace();

            // In an unrecoverable state, we must exit the application
            System.exit(1);
        }

        // Attempts to get the data back from the Hello Server
        try {
            // Sets up the packet for where the response is going to be
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            // Blocks until the packet has been received
            clientSocket.receive(receivePacket);

            // Displays the response to the screen, also limiting the length of what is displayed to the screen
            System.out.println(new String(receivePacket.getData(), 0, receivePacket.getLength()));

        } catch (IOException e) {
            // An error happened with attempting to get the packet
            e.printStackTrace();

            // In an unrecoverable state, we must exit the application
            System.exit(1);
        }

        // We close the socket so that it can be used somewhere else, although the operating system should do this by
        //  default
        clientSocket.close();

        return;
    }
}
