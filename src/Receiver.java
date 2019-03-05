import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Receiver extends Thread {
    private ServerSocket serverSocket;
    private LamportMessage message;
    private boolean running;
    private Queue<String> incomingQueue;


    public Receiver(ServerSocket serverSocket) {
        running = true;
        this.serverSocket = serverSocket;
        incomingQueue = new LinkedList<>();
    }

    @Override
    public void run() {
        super.run();
        try {
            System.out.println("Initiating connection...");
            Socket listenTo = serverSocket.accept();
            System.out.println("Connection established");
            BufferedReader reader = new BufferedReader(new InputStreamReader(listenTo.getInputStream()));
            String temporary = reader.readLine();
            System.out.println(temporary);
            if (temporary.contains(" ")) {
                String[] opponentInfo = temporary.split(" ");
                Main.opponent.setName(opponentInfo[1]);
                Main.opponent.setXpos(Integer.parseInt(opponentInfo[2]));
                Main.opponent.setYpos(Integer.parseInt(opponentInfo[3]));
                Main.opponent.setDirection(opponentInfo[4]);
                System.out.println("Connected with Player: " + Main.opponent.name);
                Main.scoreList.setText(Main.getScoreList());
                Main.connectedAction();
            }
            reader.close();
            listenTo.close();
            serverSocket.close();
            while (running) {
                serverSocket = new ServerSocket(6064);
                listenTo = serverSocket.accept();
                BufferedReader incoming = new BufferedReader(new InputStreamReader(listenTo.getInputStream()));
                String received = incoming.readLine();
                if (received.contains("MOVE")) {
                    String[] seperated = received.split(" ");
                    Main.playerMoved(Main.opponent,
                            Integer.parseInt(seperated[1]) - Main.opponent.xpos,
                            Integer.parseInt(seperated[2]) - Main.opponent.ypos,
                            seperated[3]);
                } else if (received.contains("POINT")) {
                    String[] seperated = received.split(" ");
                    if (received.contains(Main.me.name)) {
                        Main.me.setPoint(Integer.parseInt(seperated[2]));
                    } else if (received.contains(Main.opponent.name)) {
                        Main.opponent.setPoint(Integer.parseInt(seperated[2]));
                    }
                }
                incoming.close();
                listenTo.close();
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void setCLOCK() {
        Main.CLOCK = 1 + Math.max(Main.CLOCK, message.gettStamp());
    }
}
