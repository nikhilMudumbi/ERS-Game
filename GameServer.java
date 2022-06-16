import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;

public class GameServer {

    private ServerSocket ss;
    private int numPlayers = 0;
    private int maxPlayers = 2;
    private ServerSideConnection[] players;
    private ArrayList<SlapAction> slaps = new ArrayList<>();
    private ArrayList<Card> fullDeck = new ArrayList<>();
    private CentralDeck centralDeck = new CentralDeck();
    private boolean[] inGame = new boolean[maxPlayers];
    private int facesLeft = 0;
    private boolean newFace = false;
    private int facePlacer = -1;
    private boolean gameOver = false;
    private int winner = 0;
    private int tempWinner;

    public GameServer() {
        System.out.println("-----Game Server-----");
        numPlayers = 0;
        try {
            ss = new ServerSocket(51734);
        }
        catch (IOException ex) {
            System.out.println("IOException from GameServer Constructor");
        }
    }

    public void acceptConnections() {
        try {
            System.out.println("Waiting for connections...");
            while (numPlayers < maxPlayers) {
                Socket s = ss.accept();
                numPlayers++;
                System.out.println("Player #" + numPlayers + " has connected.");
                ServerSideConnection ssc = new ServerSideConnection(s, numPlayers);
                players[numPlayers-1] = ssc;
                Thread t = new Thread(ssc);
                t.start();
            }
            System.out.println("We now have " + maxPlayers + " players. No longer accepting connections.");
        }
        catch (IOException ex) {
            System.out.println("IOException from acceptConnections");
        }
    }

    private class ServerSideConnection implements Runnable {

        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int playerID;

        public ServerSideConnection(Socket s, int id) {
            socket  = s;
            playerID = id;
            try {
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            }
            catch (IOException ex) {
                System.out.println("IOException from run() SSC constructor");
            }
        }

        public void run() {
            try {
                dataOut.writeInt(playerID);
                dataOut.flush();

                while (true) {
                    /* if (dataIn.available() > 0) {
                        int signal = dataIn.readInt();
                        LocalTime time = LocalTime.now();
                        if (signal == 1) {
                            slaps.add(new SlapAction(time,playerID));
                            // rankSlaps();
                        }
                    } */
                }
            }
            catch (IOException ex) {
                System.out.println("IOException from run() SSC");
            }
        }

        public void sendMessage(String text) {
            try {
                dataOut.writeUTF(text);
                dataOut.flush();
            }
            catch (IOException ex) {
                System.out.println("IOException from sendMessage() ssc");
            }
        }

        public void sendInt(int num) {
            try {
                dataOut.writeInt(num);
                dataOut.flush();
            }
            catch (IOException ex) {
                System.out.println("IOException from sendInt() ssc");
            }
        }

        public void sendCard(Card card) {
            try {
                dataOut.writeInt(card.getNumber());
                dataOut.writeUTF(card.getSuit());
                dataOut.flush();
            }
            catch (IOException ex) {
                System.out.println("IOException from sendCard() ssc");
            }
        }

        public int receiveInt() {
            try {
                int num = dataIn.readInt();
                return num;
            }
            catch (IOException ex) {
                System.out.println("IOException from receiveInt() ssc");
            }
            return 0;
        }

        public String receiveString() {
            try {
                String str = dataIn.readUTF();
                return str;
            }
            catch (IOException ex) {
                System.out.println("IOException from receiveString() ssc");
            }
            return "";
        }

        public void readSlap() {
            try {
                if (dataIn.available() > 0) {
                    System.out.println("Slap occured");
                    long time = dataIn.readLong();
                    int player = playerID;
                    slaps.add(new SlapAction(time,player));
                }
            }
            catch (IOException ex) {
                System.out.println("IOException from readSlap() ssc");
            }
        }
    }

    public void rankSlaps() {
        Collections.sort(slaps);
        System.out.println("Player #" + slaps.get(0).player + " slapped first!");
        slaps.clear();
    }

    public void printMessage(String message) {
        for (ServerSideConnection player : players) {
            player.sendMessage(message);
        }
    }

    public void dealCards() {
        int quotient = 52 / maxPlayers;
        for (int i = 0; i < maxPlayers - (52 - quotient*maxPlayers); i++) {
            players[i].sendInt(quotient);
        }
        for (int i = maxPlayers - (52 - quotient*maxPlayers); i < maxPlayers; i++) {
            players[i].sendInt(quotient + 1);
        }

        int currentPlayer = 0;
        while (fullDeck.size() > 0) {
            players[currentPlayer].sendCard(fullDeck.get(fullDeck.size()-1));
            fullDeck.remove(fullDeck.size()-1);
            currentPlayer++;
            currentPlayer %= maxPlayers;
        }
    }

    public void initializeDeck() {
        String[] suitList = {"clubs", "spades", "diamonds", "hearts"};
        for (int i = 1; i <= 13; i++) {
            for (String suit : suitList) {
                fullDeck.add(new Card(i,suit));
            }
        }
        Collections.shuffle(fullDeck);
    }

    public Card playCard() {
        Card card = fullDeck.get(fullDeck.size()-1);
        fullDeck.remove(fullDeck.size()-1);
        centralDeck.addTop(card);
        return card;
    }

    public Card receiveCard(int player) {
        int num = players[player].receiveInt();
        String suit = players[player].receiveString();
        Card card = new Card(num, suit);
        centralDeck.addTop(card);
        return card;

    }

    public void receiveBurnCard(int player) {
        int num = players[player].receiveInt();
        String suit = players[player].receiveString();
        Card card = new Card(num, suit);
        centralDeck.burnBottom(card);
    }

    public void sendIntEveryone(int num) {
        for (ServerSideConnection player : players) {
            player.sendInt(num);
        }
    }

    public void runRound(int playerNum) throws InterruptedException {
        tempWinner = -1;
        boolean sendImmediately = false;
        // STEP 2.1 - SEND THE CURRENT PLAYER NUMBER
        sendIntEveryone(playerNum+1);
        Card card = receiveCard(playerNum);
        if (card.getNumber() > 10) {
            facesLeft = card.getNumber() - 10;
            newFace = true;
            facePlacer = playerNum;
        }
        else if (card.getNumber() == 1) {
            facesLeft = 4;
            newFace = true;
            facePlacer = playerNum;
        }
        else {
            if (facesLeft > 0) {
                facesLeft--;
                if (facesLeft == 0) {
                    sendImmediately = true;
                }
            }
            newFace = false;
        }

        // STEP 2.2 - SEND THE CARD JUST PLAYED
        for (ServerSideConnection player : players) {
            player.sendCard(centralDeck.getTop());
        }
        System.out.println("GameServer - card just played sent");
        Thread.sleep(2000);
        for (ServerSideConnection player : players) {
            player.readSlap();
        }
        if (centralDeck.slappable() && !slaps.isEmpty()) {
            System.out.println("Slappable and nonempty");
            Collections.sort(slaps);
            printMessage("slappable");
            int slapper = slaps.get(0).player;
            tempWinner = slapper-1;
            sendIntEveryone(slapper);
            players[slapper-1].sendInt(centralDeck.length);
            facesLeft = 0;
            newFace = false;
            while (!centralDeck.isEmpty()) {
                players[slapper-1].sendCard(centralDeck.removeTop());
            }
        }
        else if (!centralDeck.slappable() && !slaps.isEmpty()) {
            System.out.println("Not slappable and nonempty");
            printMessage("not slappable");
            sendIntEveryone(slaps.size());
            System.out.println(slaps.size());
            for (SlapAction slapAction : slaps) {
                sendIntEveryone(slapAction.player);
                System.out.println("sending " + slapAction.player);
            }
            for (SlapAction slapAction : slaps) {
                receiveBurnCard(slapAction.player - 1);
            }
        }
        else {
            printMessage("nothing here");
        }

        if (sendImmediately) {
            players[facePlacer].sendInt(1);
            for (ServerSideConnection player : players) {
                if (!player.equals(players[facePlacer])) {
                    player.sendInt(2);
                }
            }
            players[facePlacer].sendInt(centralDeck.length);
            while (!centralDeck.isEmpty()) {
                players[facePlacer].sendCard(centralDeck.removeTop());
            }
        }
        
        else {
            sendIntEveryone(0);
            System.out.println("facer == 0");
        }

        int numberLeft = 0;
        int[] deckSizes = new int[maxPlayers];
        // STEP 2.3 - RECEIVE CURRENT DECK SIZES
        for (int i = 0; i < maxPlayers; i++) {
            ServerSideConnection player = players[i];
            int sizeOfDeck = player.receiveInt();
            deckSizes[i] = sizeOfDeck;
            if (sizeOfDeck > 0) {
                inGame[player.playerID - 1] = true;
                numberLeft++;
            }
            else {
                inGame[player.playerID - 1] = false;
            }
        }

        // STEP 2.4 - SEND CURRENT DECK SIZES
        sendIntEveryone(maxPlayers);
        for (int sizes : deckSizes) {
            sendIntEveryone(sizes);
        }


        

        if (numberLeft == 1 && maxPlayers > 1) {
            gameOver = true;
            for (int i = 0; i < maxPlayers; i++) {
                if (deckSizes[i] > 0) {
                    winner = i;
                }
            }
        }

        slaps.clear();


    }

    public static void main(String[] args) throws InterruptedException {
        Scanner reader = new Scanner(System.in);
        GameServer gs = new GameServer();
        for (int i = 0; i < gs.maxPlayers; i++) {
            gs.inGame[i] = true;
        }
        int maxPlayers = gs.maxPlayers;
        gs.players = new ServerSideConnection[maxPlayers];
        gs.acceptConnections();
        gs.initializeDeck();
        // STEP 1 - DEALING CARDS
        gs.dealCards();

        int[] deckSizes = new int[maxPlayers];
        for (int i = 0; i < maxPlayers; i++) {
            ServerSideConnection player = gs.players[i];
            int sizeOfDeck = player.receiveInt();
            deckSizes[i] = sizeOfDeck;
        }

        gs.sendIntEveryone(maxPlayers);
        for (int sizes : deckSizes) {
            gs.sendIntEveryone(sizes);
        }

        int currentPlayer = 0;
        while (true) {
            if (gs.tempWinner != -1) {
                currentPlayer = gs.tempWinner;
            }
            if (!gs.inGame[currentPlayer]) {
                currentPlayer++;
                currentPlayer %= gs.maxPlayers;
                continue;
            }
            gs.printMessage("It is Player " + (currentPlayer+1) + "'s turn");
            // STEP 2 - RUN A ROUND
            gs.runRound(currentPlayer);
            currentPlayer++;
            if (gs.facesLeft > 0 && !gs.newFace) {
                currentPlayer--;
            }
            currentPlayer %= gs.maxPlayers;

            if (gs.gameOver) {
                gs.printMessage("over");
                gs.printMessage("Game Over! Player " + (gs.winner+1) + " wins!");
                break;
            }
            else {
                gs.printMessage("keep going");
            }
        }
    }
}