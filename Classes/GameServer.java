package Classes;

import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;

public class GameServer {

    private ServerSocket ss;
    private int numPlayers;
    private ServerSideConnection player1;
    private ServerSideConnection player2;
    private ArrayList<SlapAction> slaps = new ArrayList<>();

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
            int maxPlayers = 2;
            while (numPlayers < maxPlayers) {
                Socket s = ss.accept();
                numPlayers++;
                System.out.println("Player #" + numPlayers + " has connected.");
                ServerSideConnection ssc = new ServerSideConnection(s, numPlayers);
                if (numPlayers == 1) {
                    player1 = ssc;
                }
                else {
                    player2 = ssc;
                }
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
        
    }

    public void rankSlaps() {
        Collections.sort(slaps);
        System.out.println("Player #" + slaps.get(0).player + " slapped first!");
        slaps.clear();
    }

    public static void main(String[] args) throws InterruptedException {
        GameServer gs = new GameServer();
        gs.acceptConnections();
        Thread.sleep(15000);
        if (!gs.slaps.isEmpty()) {
            Collections.sort(gs.slaps);
            System.out.println("Player #" + gs.slaps.get(0).player + " slapped first!");
            gs.slaps.clear();
        }
    }
}