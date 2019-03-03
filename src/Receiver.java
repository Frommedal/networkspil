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
            if (temporary.contains(",")) {
                String[] opponentInfo = temporary.split(" ");
                Main.opponent.setName(opponentInfo[0]);
                Main.opponent.setXpos(Integer.parseInt(opponentInfo[1]));
                Main.opponent.setYpos(Integer.parseInt(opponentInfo[2]));
                Main.opponent.setDirection(opponentInfo[3]);
                System.out.println("Connected with Player: " + Main.opponent.name);
            }
            reader.close();
            while (running) {
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
