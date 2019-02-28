import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Receiver extends Thread {
    //TODO:
    private Socket listenTo;
    private String message;

    public Receiver(Socket listenTo) {
        this.listenTo = listenTo;
    }

    @Override
    public void run() {
        super.run();
        boolean running = true;

        while (running) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(listenTo.getInputStream()));
                String received = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
