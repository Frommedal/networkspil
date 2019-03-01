import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionRequester extends Thread {
    private Socket sendTo;
    private String message;
    private boolean running;

    public ConnectionRequester(Socket sendTo, String message) {
        this.sendTo = sendTo;
        this.message = message;
        running = true;
    }

    @Override
    public void run() {
        super.run();
        while (running) {
            try {
                DataOutputStream outputStream = new DataOutputStream(sendTo.getOutputStream());
                outputStream.writeBytes(message + "\n");

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}