import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionReceiver extends Thread {
    private ServerSocket serverSocket;
    private Socket listenTo;
    private Main main;
    private boolean running;

    public ConnectionReceiver(Socket listenTo, ServerSocket serverSocket) {
        this.listenTo = listenTo;
        running = true;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        super.run();
        try {
            System.out.println("Initiating connection...");
            listenTo = serverSocket.accept();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(listenTo.getInputStream()));
                String temporary = reader.readLine();
                if (temporary.contains(",")) {
                    String[] opponentInfo = temporary.split(",");
                    Main.opponent.setName(opponentInfo[0]);
                    Main.opponent.setXpos(Integer.parseInt(opponentInfo[1]));
                    Main.opponent.setYpos(Integer.parseInt(opponentInfo[2]));
                    Main.opponent.setDirection(opponentInfo[3]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Connected with Player: " + Main.opponent.name);
            while (running) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
