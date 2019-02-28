import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionRequester extends Thread {
    private Socket sendTo;
    private String message;

    public ConnectionRequester(Socket sendTo, String message) {
        this.sendTo = sendTo;
        this.message = message;
    }

    @Override
    public void run() {
        super.run();
        try {
            DataOutputStream outputStream = new DataOutputStream(sendTo.getOutputStream());
            outputStream.writeBytes(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}