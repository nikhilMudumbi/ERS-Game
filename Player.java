import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Player extends JFrame {

    private int width;
    private int height;
    private Container contentPane;
    private JTextArea message;
    private JButton[] pDeckButtons;
    private JButton cDeckButton;
    private int numPlayers;
    private int numCards = 0;
    

    private ClientSideConnection csc;
    private int playerID;
    private int otherPlayer;
    private PlayerDeck deck = new PlayerDeck();


    public Player(int w, int h, int num) {
        numPlayers = num;
        width = w;
        height = h;
        pDeckButtons = new JButton[numPlayers];
        contentPane = this.getContentPane();
        message = new JTextArea();
        for (int i = 0; i < numPlayers; i++) {
            pDeckButtons[i] = new JButton("Player " + (i+1) + " Deck");
        }
        cDeckButton = new JButton("Central Deck");
    }

    public void setUpGUI() {
        this.setSize(width, height);
        this.setTitle("Player #: " + playerID);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        contentPane.setLayout(new GridLayout(1,5));
        contentPane.add(message);
        message.setText("Ready to play ERS?");
        message.setWrapStyleWord(true);
        message.setLineWrap(true);
        message.setEditable(false); 
        for (int i = 0; i < numPlayers; i++) {
            contentPane.add(pDeckButtons[i]);
        }
        contentPane.add(cDeckButton);
        this.setVisible(true);
    }

    public void connectToServer() {
        csc = new ClientSideConnection();
    }

    public void updateMessage(String str) {
        message.setText(str);
    }

    public void setUpButtons() {
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JButton b = (JButton) ae.getSource();
                boolean playerButton = false;
                for (JButton button : pDeckButtons) {
                    if (b.equals(button)) {
                        System.out.println("clicked");
                        playerButton = true;
                        disablePlayerButton();
                        csc.playCard();
                    }
                }
                if (!playerButton) {
                    disableCentralButton();
                    csc.sendSlap();
                }
            }
        };

        for (int i = 0; i < pDeckButtons.length; i++) {
            if (i != playerID-1 || playerID != 1) {
                pDeckButtons[i].setEnabled(false);
            }
        }

        cDeckButton.setEnabled(false);

        for (JButton button : pDeckButtons) {
            button.addActionListener(al);
        }
        cDeckButton.addActionListener(al);
    }

    public void enablePlayerButton() {
        pDeckButtons[playerID - 1].setEnabled(true);
    }

    public void disablePlayerButton() {
        pDeckButtons[playerID - 1].setEnabled(false);
    }

    public void enableCentralButton() {
        cDeckButton.setEnabled(true);
    }

    public void disableCentralButton() {
        cDeckButton.setEnabled(false);
    }


    /*public void startReceivingMessages() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                while(true) {
                    csc.receiveMessage();
                }
            }
        });
        t.start();
    } */

    public void updatePlayerButton() {
        pDeckButtons[playerID - 1].setText("Player " + (playerID) + " Deck \r\n " + (deck.length) + " cards");
    }

    public void updatePlayerButton(int i, int size) {
        pDeckButtons[i].setText("Player " + (i+1) + " Deck \r\n " + (size) + " cards");
    }

    public void updateCentralButton(Card card) {
        if (card.getColor().equals("black")) {
            cDeckButton.setForeground(Color.BLACK);
        }
        else {
            cDeckButton.setForeground(Color.RED);
        }
        cDeckButton.setText(card.toString());
    }

    public void updateCentralButton(String str) {
        cDeckButton.setText(str);
    }

    public void slapResult() {
        try {
            String slappable = csc.receiveString();
            disableCentralButton();
            if (slappable.equals("slappable")) {
                int slapper = csc.receiveInt();
                updateMessage("Player " + slapper + " correctly slapped!");
                if (slapper == playerID) {
                    int numCards = csc.receiveInt();
                    for (int i = 0; i < numCards; i++) {
                        csc.receiveCard();
                    }
                }
                if (slapper != -1) {
                    updateCentralButton("");
                }
                Thread.sleep(2000);
            }
            else if (slappable.equals("not slappable")) {
                String text = "Players ";
                int numSlappers = csc.receiveInt();
                System.out.println("received " + numSlappers);
                boolean slappedIncorrectly = false;
                for (int i = 0; i < numSlappers; i++) {
                    int player = csc.receiveInt();
                    System.out.println("received " + player);
                    text += player + ", ";
                    if (player == playerID) {
                        slappedIncorrectly = true;
                        System.out.println("here :0");
                    }
                }
                text += "all slapped incorrectly!";
                updateMessage(text);
                System.out.println(text);
                if (slappedIncorrectly) {
                    if (deck.length > 0) {
                        csc.sendCard(deck.burnTop());
                        updatePlayerButton();
                    }
                }
                Thread.sleep(2000);
            }
        }
        catch (Exception e) {
            System.out.println("Exception in slapResult");
        }
    }

    private class ClientSideConnection {
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        public ClientSideConnection() {
            System.out.println("---Client---");
            try {
                socket = new Socket("10.31.32.122", 51734);
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                playerID = dataIn.readInt();
                System.out.println("Connected to server as Player # " + playerID + ".");
            }
            catch (IOException ex) {
                System.out.println("IOException from CSC constructor");
            }
        }

        public void sendSlap() {
            try {
                LocalTime time = LocalTime.now();
                dataOut.writeLong(time.toNanoOfDay());
                dataOut.flush();
            }
            catch (IOException ex) {
                System.out.println("IOException from sendSlap() CSC");
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
                System.out.println("here, play card");
                Card card = deck.removeTop();
                System.out.println(deck.length);
                dataOut.writeInt(card.getNumber());
                dataOut.writeUTF(card.getSuit());
                dataOut.flush();
            }
            catch (IOException ex) {
                System.out.println("IOException from playCard() CSC");
            }
        }

        public void sendCard(Card card) {
            try {
                dataOut.writeInt(card.getNumber());
                dataOut.writeUTF(card.getSuit());
                dataOut.flush();
            }
            catch (IOException ex) {
                System.out.println("IOException from sendCard() CSC");
            }
        }

        public void sendMessage(String text) {
            try {
                dataOut.writeUTF(text);
                dataOut.flush();
            }
            catch (IOException ex) {
                System.out.println("IOException from sendMessage() CSC");
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

        public String receiveString() {
            String text;
            try {
                text = dataIn.readUTF();
                return text;
            }
            catch (IOException ex) {
                System.out.println("IOException from receiveMessage() CSC");
            }
            return "";
        }

        public Card receiveCard() {
            try {
                int cardNum = dataIn.readInt();
                String cardSuit = dataIn.readUTF();
                Card card = new Card(cardNum, cardSuit);
                deck.addCard(card);
                return card;
            }
            catch (IOException ex) {
                System.out.println("IOException from receiveCard() CSC");
            }
            return new Card(0,"diamond");
        }

        public Card readCard() {
            try {
                int cardNum = dataIn.readInt();
                String cardSuit = dataIn.readUTF();
                Card card = new Card(cardNum, cardSuit);
                return card;
            }
            catch (IOException ex) {
                System.out.println("IOException from readCard() CSC");
            }
            return new Card(0,"diamond");
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

        public void sendInt(int num) {
            try {
                dataOut.writeInt(num);
                dataOut.flush();
            }
            catch (IOException ex) {
                System.out.println("IOException from sendInt() CSC");
            }
        }
    }

    public static void main(String[] args) {
        Player p = new Player(700, 350, 2);
        p.connectToServer();
        p.setUpGUI();
        p.setUpButtons();
        // System.out.println("playerID: " + p.playerID);
        // p.startReceivingMessages();
        Scanner reader = new Scanner(System.in);
        // STEP 1 - RECEIVING DEALT CARDS
        int numOfCards = p.csc.receiveInt();
        for (int i = 0; i < numOfCards; i++) {
            p.csc.receiveCard();
        }

        p.csc.sendInt(p.deck.length);

        int numPlayers = p.csc.receiveInt();
        for (int i = 0; i < numPlayers; i++) {
            int intReceived = p.csc.receiveInt();
            p.updatePlayerButton(i, intReceived);
        }
        //p.updatePlayerButton();
        while (true) {
            p.updateMessage(p.csc.receiveString());
            // STEP 2 - RUN A ROUND
            // STEP 2.1 - SEND THE CURRENT PLAYER NUMBER
            int currentPlayer = p.csc.receiveInt(); // whose turn is it?
            System.out.println(currentPlayer);
            // STEP 2.1.5 - IF YOUR TURN, BUTTON ENABLED, SEND CARD
            if (currentPlayer == p.playerID) {
                p.enablePlayerButton();
            }
            // STEP 2.2 - RECEIVE THE CARD JUST PLAYED 
            Card card = p.csc.readCard();
            if (currentPlayer == p.playerID) {
                System.out.println(p.deck.length);
                p.updatePlayerButton();
            }
            System.out.println("Player - card just played received");
            p.updateCentralButton(card);
            p.enableCentralButton();
            p.slapResult();

            int facer = p.csc.receiveInt();
            if (facer == 1) {
                int numCards = p.csc.receiveInt();
                for (int i = 0; i < numCards; i++) {
                    p.csc.receiveCard();
                }
                p.updateCentralButton("");
            }
            if (facer == 2) {
                p.updateCentralButton("");
            }

            // STEP 2.3 - SEND CURRENT DECK SIZE
            p.csc.sendInt(p.deck.length);
            System.out.println(p.deck.length);

            // STEP 2.4 - RECEIVE CURRENT DECK SIZES
            int numPpl = p.csc.receiveInt();
            for (int i = 0; i < numPpl; i++) {
                int intReceived = p.csc.receiveInt();
                p.updatePlayerButton(i, intReceived);
            }


            String gameStatus = p.csc.receiveString();
            if (gameStatus.equals("over")) {
                p.updateMessage(p.csc.receiveString());
                break;
            }
        }
        /*p.startReceivingMessages();
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
        }*/
    }

}