package net.dalesalter;

import java.net.InetAddress;

/**
 * Peer
 *
 * Simple data class to hold all the information that a peer would need
 *  to have to communicate to and identify it
 *
 * This program has been put together and inspired from various places
 *  [1] - https://docs.oracle.com/javase/tutorial/essential/environment/cmdLineArgs.html
 *  [2] - http://docs.oracle.com/javase/1.5.0/docs/api/java/net/InetAddress.html#getByName%28java.lang.String%29
 *  [3] - http://www.rgagnon.com/javadetails/java-check-if-a-filename-is-valid.html
 */
public class Peer {
    private InetAddress peerIP;
    private int peerPort;
    private int peerId;
    private String peerName;

    // We can give the peers unique IDs, this keeps track of them
    private static int peerCount = 0;

    // Public constructor, we can instantiate this class
    public Peer(String peerName, InetAddress peerIP, int peerPort){
        this.peerName = peerName;
        this.peerIP = peerIP;
        this.peerPort = peerPort;

        this.peerId = peerCount++;
    }

    // Required getters to this class
    public InetAddress getPeerIP() { return peerIP; }
    public int getPeerPort() { return peerPort; }
    public int getPeerId() { return peerId; }
    public String getPeerName() { return peerName; }

    // Override the string to give a nicer printout of what this class actually contains
    @Override
    public String toString(){
        return "Peer " + peerId + ", " + peerName + " \t<InetAddress = " + peerIP + ",  Port = " + peerPort + ">";
    }
}
