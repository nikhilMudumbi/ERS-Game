package Classes;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Player {

    private ClientSideConnection csc;
    private int playerID;
    private int otherPlayer;

    public void connectToServer() {
        csc = new ClientSideConnection();
    }


    public void startReceivingMessages() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    csc.receiveMessage();
                    System.out.println("here8");
                }
            }
        });
        t.start();
    }

    private class ClientSideConnection {
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        public ClientSideConnection() {
            System.out.println("---Client---");
            try {
                socket = new Socket("10.31.1.24", 51734);
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                playerID = dataIn.readInt();
                System.out.println("Connected to server as Player #" + playerID + ".");
            }
            catch (IOException ex) {
                System.out.println("IOException from CSC constructor");
            }
        }

        public void sendCommand(int text) {
            try {
                dataOut.writeInt(text);
                dataOut.flush();
            }
            catch (IOException ex) {
                System.out.println("IOException from sendButtonNum() CSC");
            }
        }

        public void receiveMessage() {
            String text;
            try {
                text = dataIn.readUTF();
                System.out.println(text);
            }
            catch (IOException ex) {
                System.out.println("IOException from receiveMessage() CSC");
            }
        }
    }

    public static void main(String[] args) {
        Player p = new Player();
        p.connectToServer();
        System.out.println("playerID: " + p.playerID);
        p.startReceivingMessages();
        Scanner reader = new Scanner(System.in);
        if (reader.hasNextLine()) {
            String text = reader.nextLine();
            if (text.equals("slap")) {
                p.csc.sendCommand(1);
            }
        }
    }

}