import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Player {

    private ClientSideConnection csc;
    private int playerID;
    private int otherPlayer;
    private PlayerDeck deck;

    public void connectToServer() {
        csc = new ClientSideConnection();
    }


    public void startReceivingMessages() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    csc.receiveMessage();
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
                socket = new Socket("10.31.34.22", 51734);
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

        public void playCard() {
            try {
                Card card = deck.removeTop();
                dataOut.writeInt(card.getNumber());
                dataOut.writeUTF(card.getSuit());
                dataOut.flush();
            }
            catch (IOException ex) {
                System.out.println("IOException from playCard() CSC");
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

        public void receiveCard() {
            try {
                int cardNum = dataIn.readInt();
                String cardSuit = dataIn.readUTF();
                deck.addCard(new Card(cardNum, cardSuit));
            }
            catch (IOException ex) {
                System.out.println("IOException from receiveCard() CSC");
            }
        }

        public int receiveInt() {
            try {
                int num = dataIn.readInt();
                return num;
            }
            catch (IOException ex) {
                System.out.println("IOException from receiveInt() CSC");
            }
            return 0;
        }
    }

    public static void main(String[] args) {
        Player p = new Player();
        p.connectToServer();
        System.out.println("playerID: " + p.playerID);
        p.startReceivingMessages();
        Scanner reader = new Scanner(System.in);
        int numOfCards = p.csc.receiveInt();
        for (int i = 0; i < numOfCards; i++) {
            p.csc.receiveCard();
        }
        // initial player deck is now constructed
        while (true) {
            String text = reader.nextLine();
            if (text.equals("s")) {
                p.csc.sendCommand(1);
            }
        }
    }

}