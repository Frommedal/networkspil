import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Receiver extends Thread {
    private ServerSocket serverSocket;
    private boolean running;
    private Queue<String> incomingQueue;
    private Requester requester;


    Receiver(ServerSocket serverSocket, Requester requester) {
        running = true;
        this.serverSocket = serverSocket;
        incomingQueue = new LinkedList<>();
        this.requester = requester;
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
            serverSocket = new ServerSocket(6063);
            listenTo = serverSocket.accept();
            BufferedReader incoming = new BufferedReader(new InputStreamReader(listenTo.getInputStream()));
            while (running) {
                String received = incoming.readLine();
                while (received != null) {
                    incomingQueue.add(received);
                    received = incoming.readLine();
                }
                while (incomingQueue.size() > 0) {
                    if (incomingQueue.peek() != null && incomingQueue.peek().contains("MOVE")) {
                        String removed = incomingQueue.remove();
                        String[] seperated = removed.split(" ");
                        Platform.runLater(() -> Main.playerMoved(Main.opponent,
                                Integer.parseInt(seperated[1]) - Main.opponent.xpos,
                                Integer.parseInt(seperated[2]) - Main.opponent.ypos,
                                seperated[3]));
                    } else if (incomingQueue.peek() != null && incomingQueue.peek().contains("POINT")) {
                        String removed = incomingQueue.remove();
                        String[] seperated = removed.split(" ");
                        if (removed.contains(Main.me.name)) {
                            Main.me.setPoint(Integer.parseInt(seperated[2]));
                        } else if (removed.contains(Main.opponent.name)) {
                            Main.opponent.setPoint(Integer.parseInt(seperated[2]));
                        }
                    } else if (incomingQueue.peek() != null && incomingQueue.peek().contains("REQUEST")) {
                        try {
                            String removed = incomingQueue.remove();
                            String[] seperated = removed.split(" ");
                            int k = Integer.parseInt(seperated[1]);
                            int j = Integer.parseInt(seperated[2]);
                            Main.Our_Sequence_Number = Math.max(Main.Our_Sequence_Number, k);
                            Semaphore Shared_Variables = new Semaphore(1);
                            Shared_Variables.acquire();
                            boolean Defer_it = Main.Requesting_Critical_Section
                                    && (
                                        (k > Main.Our_Sequence_Number)
                                                || (k == Main.Our_Sequence_Number && j > Main.My_Unique_Number)
                            );
                            Shared_Variables.release();
                            if (Defer_it) {
                                Main.Reply_Deferred[j] = true;
                            } else {
                                requester.addMessageToOutgoingQueue("REPLY " + j);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (incomingQueue.peek() != null && incomingQueue.peek().contains("REPLY")) {
                        Main.Outstanding_Reply_Count -= 1;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}