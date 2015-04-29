package net.dalesalter;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * FileServer
 *
 * Created by Dale Salter (9724 397) on 29/04/2015.
 *
 * This program has been put together and inspired from various places
 *  [1] - https://docs.oracle.com/javase/tutorial/essential/environment/cmdLineArgs.html
 *  [2] - http://www.cs.uic.edu/~troy/spring05/cs450/sockets/WebServer.java
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
    private static void setUp(){
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
     * [2] - Starts running the server, listening on the previously set up socket
     */
    private static void runServer(){
        // The IP address and port number of the person who is setting up the TCP connection
        InetAddress IPAddress = null;
        int port = 0;

        // The connection of the client
        Socket connectionSocket = null;

        String requestMessageLine;
        String fileName;

        try {
            System.out.println("Waiting for a client!");
            connectionSocket = serverSocket.accept();
            System.out.println("Client connected!");

            BufferedReader inFromClient = new BufferedReader (
                    new InputStreamReader(connectionSocket.getInputStream()));

            DataOutputStream outToClient = new DataOutputStream (
                    connectionSocket.getOutputStream());

            requestMessageLine = inFromClient.readLine();
            System.out.println ("Request: " + requestMessageLine);
            StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine);


            // check for Send request
            if (tokenizedLine.nextToken().equals("Send"))
            {
                fileName = tokenizedLine.nextToken();

                // remove leading slash from line if exists
                if (fileName.startsWith("/") == true)
                    fileName = fileName.substring(1);

                // access the requested file
                File file = new File(fileName);

                // convert file to a byte array
                // int numOfBytes = (int) file.length();
                FileInputStream inFile = new FileInputStream ("store/" + fileName);
                //byte[] fileInBytes = new byte[numOfBytes];
                //inFile.read(fileInBytes);
                // inFile.close();

                System.out.println("#2");

                // System.out.println(fileInBytes);

                // Send reply
                byte[] buffer = new byte[1024];

                int read;
                while((read = inFile.read(buffer)) != -1){
                    outToClient.write(buffer, 0, read);
                }

                // outToClient.write(fileInBytes, 0, numOfBytes);
                //outToClient.flush();
                //outToClient.close();



                connectionSocket.close();

                System.out.println("Finished serving!");
            }
            else
            {
                System.out.println ("Bad Request Message");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    /**
     * The entry point to the server application
     * @param args
     *      args[0] the port of the server to bind onto in the format "8888"
     */
    public static void main(String[] args) {

//        System.out.println("FileServer Output!");
//
//        File newFile = new File("store/myfile.txt");
//        try {
//            FileOutputStream fileOutput = new FileOutputStream(newFile);
//            fileOutput.write((new String("lol").getBytes()));
//
//            fileOutput.close();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



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

        // Sets up the TCP port
        setUp();

        // Listens to incoming TCP connections on that port
        runServer();

        for (String arg: args)
            System.out.println(arg);

        return;
    }
}
