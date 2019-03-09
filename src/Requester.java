import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Requester extends Thread {
    private Queue<String> outgoingQueue;
    private String playerIP;
    private boolean running;

    Requester(String playerIP) {
        running = true;
        this.playerIP = playerIP;
        outgoingQueue = new LinkedList<>();
    }

    void addMessageToOutgoingQueue(String message) {
        outgoingQueue.add(message);
    }

    @Override
    public void run() {
        super.run();
        try {
            Socket sendTo = new Socket(playerIP, 6062);
            System.out.println("Attempting to connect to: " + playerIP);
            DataOutputStream outputStream = new DataOutputStream(sendTo.getOutputStream());
            outputStream.writeBytes("NAME " + Main.me.name + " " + Main.me.getXpos() + " " + Main.me.getYpos() + " " + Main.me.getDirection());
            while (running) {
                try {
                    while (outgoingQueue.size() > 0) {
                        outputStream.writeBytes(outgoingQueue.remove() + "\n");
                    }
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}