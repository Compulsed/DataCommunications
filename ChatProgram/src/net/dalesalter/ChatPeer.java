package net.dalesalter;

public class ChatPeer {

    static class ServerThread extends Thread {

        public ServerThread(){

        }

        public void run(){
            while(true)
                System.out.println("From Server thread!");
        }
    }

    static class ClientThread extends Thread {

        public ClientThread(){

        }

        public void run(){
            while(true)
                System.out.println("From Client thread!");
        }

    }

    public static void main(String[] args) {
	// write your code here
        ServerThread a = new ServerThread();
        ClientThread b = new ClientThread();

        a.start();
        b.start();

    }
}
