import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ConnectionReceiver extends Thread {
    private Socket listenTo;
    private Main main;

    public ConnectionReceiver(Socket listenTo, Main main) {
        this.listenTo = listenTo;
        this.main = main;
    }

    @Override
    public void run() {
        super.run();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(listenTo.getInputStream()));
            main.setOpponentInfo(reader.readLine().split(","));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
