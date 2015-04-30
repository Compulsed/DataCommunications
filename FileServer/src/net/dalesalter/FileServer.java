package net.dalesalter;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * FileServer
 * Used to connect to the corresponding client
 * can send files to that client
 *
 * Created by Dale Salter (9724 397) on 29/04/2015.
 *
 * This program has been put together and inspired from various places
 *  [1] - https://docs.oracle.com/javase/tutorial/essential/environment/cmdLineArgs.html
 *  [2] - http://www.cs.uic.edu/~troy/spring05/cs450/sockets/WebServer.java
 *  [3] - http://www.rgagnon.com/javadetails/java-check-if-a-filename-is-valid.html
 */

public class FileServer {

    /**
     * User may specify a port to have a socket bind to, default is 8888 if none specified
     */
    private static int inputPort = 8888;

    /**
     * Server socket is the socket that the client will connect to
     */
    private static ServerSocket serverSocket = null;

    /**
     * [2] - Sets up the socket with the previously specified port
     */
    private static void setUpConnection(){
        try {
            // Sets up a new socket where the clients can connect to, the port is the one specified by the user
            serverSocket = new ServerSocket(inputPort);
        } catch (IOException e) {
            System.err.println("Critical error, must terminate. Do you have another server bound to this port? - "
                    + inputPort);
            // In an unrecoverable state, we must exit the application
            System.exit(1);
        }
    }

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
     * [2] - Starts running the server, listening on the previously set up socket
     */
    private static void runServer(){
        // The IP address and port number of the person who is setting up the TCP connection
        InetAddress IPAddress = null;
        int port = 0;

        // The connection of the client
        Socket connectionSocket = null;

        // The message sent from the client to the server
        String requestMessageLine;

        // The filename that the client is requesting
        String fileName;

        // Used to get the handle input from the TCP connection
        BufferedReader inFromClient;
        // Used when writing to the file
        DataOutputStream outToClient;

        while (true) {
            try {
                System.out.println("Server status -> Waiting for a client!");
                connectionSocket = serverSocket.accept();
                System.out.println("Server status -> " + connectionSocket +  " -> Client connected!");

                // Sets up input stream to the network so that it accepts UTF
                inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                // Reads the line from the client
                requestMessageLine = inFromClient.readLine();
                System.out.println("Server status -> " + connectionSocket + " -> Request string: " + requestMessageLine);

                // We need to parse the response
                // Valid responses should be in the form "Show" <filename>
                StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine);

                // Get the first token and check that is equal to "Send"
                if (tokenizedLine.nextToken().equals("Send")) {
                    fileName = tokenizedLine.nextToken();

                    // remove leading slash from line if exists
                    if (fileName.startsWith("/") == true)
                        fileName = fileName.substring(1);


                    // Check if the file name could be something that can be saved
                    if(isFilenameValid(fileName)){
                        FileInputStream inFile = new FileInputStream("store/" + fileName);

                        // Sets up output stream to send binary data back over the network
                        outToClient = new DataOutputStream(connectionSocket.getOutputStream());


                        // Buffer is used to take in the file at a fixed amount at a time
                        //  and is also used to send that message to the client
                        byte[] buffer = new byte[1024];


                        // Uses to keep track of the number of bytes sent across the network
                        int totalBytesSent = 0;

                        // Keeps track of the byte location of the file
                        int read;

                        // Reads from the file, puts it into the buffer and then sends it
                        //  across the network
                        while ((read = inFile.read(buffer)) != -1) {
                            outToClient.write(buffer, 0, read);
                            totalBytesSent += read;
                        }

                        // closes the file so that it can be reponed by another application
                        inFile.close();

                        System.out.println("Server status -> " + connectionSocket + " -> Finished serving: " + fileName + ", Bytes sent: " + totalBytesSent);
                    } else {
                        System.out.println("Server status -> Unable to send: " + fileName);
                    }
                } else {
                    System.out.println("Server status -> Bad Request Message");
                }

                // Closes the TCP connection to the client
                connectionSocket.close();

                System.out.println("------------------------------------------");

            } catch (IOException e) {
                System.err.println("Something has gone wrong with the connection!");
            }
        }

    }

    /**
     * Makes a Temp file which can be used to demonstrate functionality
     */
    public static void fileSetUp(){
        System.out.println("Server status: Making mock files");

        File newFile = new File("store/myfile.txt");
        try {
            FileOutputStream fileOutput = new FileOutputStream(newFile);
            fileOutput.write((new String("0123456789").getBytes()));

            fileOutput.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();

            System.exit(1);
        }
    }

    /**
     * The entry point to the server application
     * @param args
     *      args[0] the port of the server to bind onto in the format "8888"
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

        // Creates a temp file to send
        fileSetUp();

        // Sets up the TCP port
        setUpConnection();

        // Listens to incoming TCP connections on that port
        runServer();


        return;
    }
}
