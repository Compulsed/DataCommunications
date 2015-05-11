package net.dalesalter;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dale on 11/05/2015.
 */

public class Config {
    private Config(){}

    public static HashMap<InetAddress, HashMap<Integer, Peer>>  load(String fileName) throws IOException {
        HashMap<InetAddress, HashMap<Integer, Peer>> validPeers = new HashMap<InetAddress, HashMap<Integer, Peer>>();

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        ArrayList<String> fileLines = new ArrayList<String>();

        String lineRead;
        while((lineRead = br.readLine()) != null){
            fileLines.add(lineRead);
        }

        // Dale localhost 5001
        for (String line : fileLines){
            String[] t = line.split(" ");

            String user = t[0];
            InetAddress inet = InetAddress.getByName(t[1]);
            Integer port = new Integer(Integer.parseInt(t[2]));


            // Check if inet already in hashmap, if it is not, create a new mapping
            if (!validPeers.containsKey(inet)){
                validPeers.put(inet, new HashMap<>());
            }

            HashMap<Integer, Peer> IPtoPortMapping = validPeers.get(inet);

            IPtoPortMapping.put(port, new Peer(user, inet, port));
        }

        return validPeers;
    }

}
