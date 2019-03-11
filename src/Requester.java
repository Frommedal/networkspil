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
            DataOutputStream initialOutput = new DataOutputStream(sendTo.getOutputStream());
            initialOutput.writeBytes("NAME " + Main.me.name + " " + Main.me.getXpos() + " " + Main.me.getYpos() + " " + Main.me.getDirection());
            initialOutput.close();
            sendTo.close();
            while (running) {
                try {
                    sendTo = new Socket(playerIP, 6064);
                    DataOutputStream outputStream = new DataOutputStream(sendTo.getOutputStream());
                    if (outgoingQueue.size() > 0) {
                        String out = outgoingQueue.remove();
                        System.out.println("Sending: " + out);
                        outputStream.writeBytes(out + "\n");
                    }
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

}