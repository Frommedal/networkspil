import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Requester extends Thread {
    private Socket sendTo;
    private String message;
    private LamportMessage lamportMessage;
    private String playerIP;
    private boolean running;

    public Requester(String playerIP) {
        running = true;
        this.playerIP = playerIP;
    }

    @Override
    public void run() {
        super.run();
        try {
            sendTo = new Socket(playerIP, 6061);
            System.out.println("Attempting to connect to: " + playerIP);
            DataOutputStream initialOutput = new DataOutputStream(sendTo.getOutputStream());
            initialOutput.writeBytes("NAME " + Main.me.name + " " + Main.me.getXpos() + " " + Main.me.getYpos() + " " + Main.me.getDirection());
            initialOutput.close();
            sendTo.close();
            while (running) {
                try {
                    sendTo = new Socket(playerIP, 6064);
                    DataOutputStream outputStream = new DataOutputStream(sendTo.getOutputStream());
                    outputStream.writeBytes(message + "\n");
                    outputStream.flush();
                    outputStream.close();
                    sendTo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void increamentCLOCK() {
        Main.CLOCK++;
    }
}