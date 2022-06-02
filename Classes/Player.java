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

    private class ClientSideConnection {
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        public ClientSideConnection() {
            System.out.println("---Client---");
            try {
                socket = new Socket("10.62.1.197", 51734);
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
    }

    public static void main(String[] args) {
        Player p = new Player();
        p.connectToServer();
        System.out.println("playerID: " + p.playerID);
        Scanner reader = new Scanner(System.in);
        if (reader.hasNextLine()) {
            String text = reader.nextLine();
            if (text.equals("slap")) {
                p.csc.sendCommand(1);
            }
        }
    }

}