import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver extends Thread {
    private ServerSocket serverSocket;
    private LamportMessage message;
    private Socket listenTo;
    private boolean running;


    public Receiver(ServerSocket serverSocket) {
        running = true;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        super.run();
        try {
            System.out.println("Initiating connection...");
            listenTo = serverSocket.accept();
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

                } else if (received.contains("POINT")) {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void setCLOCK() {
        Main.CLOCK = 1 + Math.max(Main.CLOCK, message.gettStamp());
    }
}
