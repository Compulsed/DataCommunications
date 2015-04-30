package net.dalesalter;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * FileClient
 * Used to connect to the corresponding sever,
 * can receive files from that server
 *
 * Created by Dale Salter (9724 397) on 29/04/2015.
 *
 * This program has been put together and inspired from various places
 *  [1] - https://docs.oracle.com/javase/tutorial/essential/environment/cmdLineArgs.html
 *  [2] - http://docs.oracle.com/javase/1.5.0/docs/api/java/net/InetAddress.html#getByName%28java.lang.String%29
 *  [3] - http://www.rgagnon.com/javadetails/java-check-if-a-filename-is-valid.html
 */
public class FileClient {

    /**
     * The user specified IP address that they believe the server is listening on
     */
    private static InetAddress serverIP = null;

    /**
     * The user specified port number that they believe the server is on
     */
    private static int serverPort = 0;

    /**
     * The TCP socket of the FileServer that we are interacting with
     */
    private static Socket socket = null;


    /**
     * [3] Used as a way of quickly determining whether a file is valid before asking for it
     * @param file  The name of the file that you want to test for
     * @return Whether given the file name, is it valid or not
     */
    public static boolean isFilenameValid(String file) {
        File f = new File(file);
        try {
            f.getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * The entry point to the client application
     * @param args
     *      args[0] IP of the corresponding server in format "192.168.1.1"
     *      args[1] Port of the corresponding server in format "8888"
     *      args[2] File name that you want to get from the server
     */
    public static void main(String[] args){
        // We must validate the commandline inputs into the application (IP address, Port number, file)
        // [1] - Arguments validation
        // [2] - Parsing the IP Address string to and INetAddress
        if(args.length == 3){
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

            // Tests whether the system can store the file name that is being asked of off of the server
            if(!isFilenameValid(args[2])){
                // The value sent is not able to be turned into a file,  therefore we must exit and let them try again
                System.err.println("The given file name '" + args[2] + "' is not a valid file name");
                System.exit(1);
            }

        }
        else {
            // Prints the response to the user so that they know how to use the application on the command line
            System.out.println("Usage: <IP of UDP Server> <Port of File server> <File name>");

            // We should exit and let them user try again
            System.exit(1);
        }

        // Writes the request message to the server
        BufferedWriter toServer;

        // The binary file response from the server
        DataInputStream fromServer;

        // The file to write the response to
        FileOutputStream toDisk;

        // The valid file string
        String fileString = args[2];
        String requestMessage;

        try {
            System.out.println("Client status -> Attempting to connect to the server!");
            // Connects to the server given the user inputted IP and Port number
            socket = new Socket(serverIP, serverPort);
            System.out.println("Client status -> " + socket +  " -> Connected!");

            // Constructs request message
            requestMessage = "Send " + fileString + "\r\n";

            // Prepares the connection to be written to
            toServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Writes to message to the buffer, preparing it to be sent to the server
            System.out.println("Client status -> " + socket + " -> Writing message!");
            toServer.write(requestMessage, 0, requestMessage.length());

            // Clears the buffer so that the server will get the request message
            //  this also stops a potential problem with the server and client
            //  locking up
            System.out.println("Client status -> " + socket + " -> Flushed message!");
            toServer.flush();


            // Prepares the file and the stream for connection
            toDisk = new FileOutputStream(new File(fileString));
            fromServer = new DataInputStream(socket.getInputStream());

            System.out.println("Client status -> " + socket + " -> Attempting disk write and network read");

            // Buffer is used to take in the file at a fixed amount at a time
            //  and is also used to read that file sent from the server
            byte[] buffer = new byte[1024];


            // Uses to keep track of the number of bytes sent across the network
            int totalsBytesReceived = 0;


            // Keeps track of the byte location of the file
            int read = 0;

            // Reads from the network, puts it into the buffer and then
            //  writes to that file
            while((read = fromServer.read(buffer, 0, 1024)) != -1){
                toDisk.write(buffer, 0, read);
                totalsBytesReceived += read;
            }

            System.out.println("Client status -> " + socket + " -> " + fileString + " written to disk, Bytes received: "
                    + totalsBytesReceived);

            // Closes the file so it can be used by another application
            toDisk.close();

            // Closes the TCP connection to the server

            socket.close();
        } catch (IOException e) {
            System.err.println("Something has gone wrong with the connection!");
        }


        return;
    }
}
