package net.dalesalter;

import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Configuration
 *
 * Helper method to set up valid peers
 *
 * This program has been put together and inspired from various places
 *  [1] - https://docs.oracle.com/javase/tutorial/essential/environment/cmdLineArgs.html
 *  [2] - http://docs.oracle.com/javase/1.5.0/docs/api/java/net/InetAddress.html#getByName%28java.lang.String%29
 *  [3] - http://www.rgagnon.com/javadetails/java-check-if-a-filename-is-valid.html
 */
public class Config {

    // Make private so we cannot construct this object and just use it for its static methods
    private Config(){}

    /**
     * Takes in a configuration file in the format
     *
     * <name> <ip> <port>
     *
     *  Then returns these as Peers which are valid
     *  This particular method of hash maps have been used because it allows for clients with the same IP address
     *  to be distinguished from each other and function transparently
     *
     * @param fileName The location of the configuration file
     * @return HapMap of IPs each containing another HashMap of each port associated with the given IP
     */
    public static HashMap<InetAddress, HashMap<Integer, Peer>>  load(String fileName) throws IOException {
        HashMap<InetAddress, HashMap<Integer, Peer>> validPeers = new HashMap<InetAddress, HashMap<Integer, Peer>>();

        // Sets up the file ready to be read, taking it in as a stream
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        // Prepares the output as an ArrayList of the input strings
        ArrayList<String> fileLines = new ArrayList<String>();

        // Reads the stream from file and then adds it to the fileLines
        String lineRead;
        while((lineRead = br.readLine()) != null){
            fileLines.add(lineRead);
        }

        // Format example: Dale localhost 5001
        // For each fileLine, we need to parse it and ass it to the configuration file
        for (String line : fileLines){
            String[] t = line.split(" ");

            String user = t[0];
            InetAddress inet = InetAddress.getByName(t[1]);
            Integer port = new Integer(Integer.parseInt(t[2]));


            // Check if inet already in hashmap, if it is not, create a new mapping
            //  we can then attach new ports to this IP
            if (!validPeers.containsKey(inet)){
                validPeers.put(inet, new HashMap<Integer, Peer>());
            }

            // The IP that we want to add the port to
            HashMap<Integer, Peer> IPtoPortMapping = validPeers.get(inet);

            // Creates the new peer based on the port of the peer, adds it to the IP map
            IPtoPortMapping.put(port, new Peer(user, inet, port));
        }

        return validPeers;
    }

}
