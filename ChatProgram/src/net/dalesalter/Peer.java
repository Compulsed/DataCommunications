package net.dalesalter;

import java.net.InetAddress;

/**
 * Created by dale on 11/05/2015.
 */
public class Peer {

    private InetAddress peerIP;
    private int peerPort;
    private int peerId;
    private String peerName;

    private static int peerCount = 0;

    public Peer(String peerName, InetAddress peerIP, int peerPort){
        this.peerName = peerName;
        this.peerIP = peerIP;
        this.peerPort = peerPort;

        this.peerId = peerCount++;
    }

    public Peer() {}

    public InetAddress getPeerIP() {
        return peerIP;
    }

    public int getPeerPort() {
        return peerPort;
    }

    @Override
    public String toString(){
        return "Peer " + peerId + ", " + peerName + " \t<InetAddress = " + peerIP + ",  Port = " + peerPort + ">";
    }
}
