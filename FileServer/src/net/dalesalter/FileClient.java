package net.dalesalter;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * FileClient
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

        // Writes the message to the server
        BufferedWriter toServer;

        DataInputStream fromServer;
        FileOutputStream toDisk;

        try {
            System.out.println("Connecting to server!");
            socket = new Socket(serverIP, serverPort);


            System.out.println("Constructing message!");
            String requestMessage = "Send " + args[2] + "\r\n";

            toServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            System.out.println("Writing message!");
            toServer.write(requestMessage, 0, requestMessage.length());

            System.out.println("Flushed message!");
            toServer.flush();



            toDisk = new FileOutputStream(new File(args[2]));
            fromServer = new DataInputStream(socket.getInputStream());


            byte[] buffer = new byte[1024];



            System.out.println("Attempting disk write!");
            int read = 0;
            while((read = fromServer.read(buffer, 0, 1024)) != -1){
                toDisk.write(buffer, 0, read);
            }



            System.out.println("Closing file!");
            toDisk.close();


            socket.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String arg : args)
            System.out.println(arg);

        System.out.println("END!");

        return;
    }
}
