import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;

public class GameServer {

    private ServerSocket ss;
    private int numPlayers;
    private ServerSideConnection[] players;
    private ArrayList<SlapAction> slaps = new ArrayList<>();
    private int maxPlayers = 2;
    private ArrayList<Card> fullDeck = new ArrayList<>();
    private CentralDeck centralDeck = new CentralDeck();

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
                    if (dataIn.available() > 0) {
                        int signal = dataIn.readInt();
                        LocalTime time = LocalTime.now();
                        if (signal == 1) {
                            slaps.add(new SlapAction(time,playerID));
                            // rankSlaps();
                        }
                    }
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

        public void sendInitialCards(ArrayList<Card> list) {
            
        }
    }

    private class SlapAction implements Comparable<SlapAction> {
        private LocalTime time;
        private int player;

        public SlapAction(LocalTime time, int player) {
            this.time = time;
            this.player = player;
        }

        @Override
        public int compareTo(SlapAction o) {
            if (!this.time.equals(o.time)) {
                return this.time.compareTo(o.time);
            }
            if (Math.random() <= 0.5) {
                return 1;
            }
            return -1;
        }

        public LocalTime getTime() {
            return time;
        }

        public int getPlayer() {
            return player;
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

    public void dealCards() {}

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

    public static void main(String[] args) throws InterruptedException {
        Scanner reader = new Scanner(System.in);
        GameServer gs = new GameServer();
        gs.players = new ServerSideConnection[gs.maxPlayers];
        gs.acceptConnections();
        gs.initializeDeck();
        // gs.dealCards();
        System.out.print("Enter 'start' to begin the game: ");
        String line = reader.nextLine();
        for (int i = 0; i < 52; i++) {
            gs.printMessage("Card just played: " + gs.playCard());
            Thread.sleep(3000);
            if (gs.centralDeck.slappable()) {
                gs.printMessage("This was slappable.");
                if (!gs.slaps.isEmpty()) {
                    Collections.sort(gs.slaps);
                    gs.printMessage("Player #" + gs.slaps.get(0).player + " slapped first!");
                    gs.slaps.clear();
                    gs.printMessage("Resetting the deck.");
                    gs.centralDeck = new CentralDeck();
                }
                else {
                    gs.printMessage("No one slapped :(");
                }
            }
            else {
                gs.printMessage("This was not slappable.");
                if (gs.slaps.isEmpty()) {
                    gs.printMessage("No one slapped, nice!");
                }
                else {
                    Collections.sort(gs.slaps);
                    String message = "Players ";
                    if (gs.slaps.size() == 1) {
                        gs.printMessage("Player " + gs.slaps.get(0).getPlayer() + " slapped ;(");
                    }
                    else {
                        for (SlapAction slapAction : gs.slaps) {
                            message += slapAction.getPlayer() + ", ";
                        }
                        message += "all slapped ;(";
                        gs.printMessage(message);
                    }
                    gs.slaps.clear();
                }
            }
            System.out.print("Enter 'next' to continue to the next card, or 'end' to end the game: ");
            line = reader.nextLine();
            if (line.equals("end")) {
                break;
            }
        }
        
        /* while (true) {
            gs.printMessage("slap now!!!");
            Thread.sleep(2000);
            if (!gs.slaps.isEmpty()) {
                Collections.sort(gs.slaps);
                gs.printMessage("Player #" + gs.slaps.get(0).player + " slapped first!");
                gs.slaps.clear();
                Thread.sleep(2000);
            }
        } */
    }
}