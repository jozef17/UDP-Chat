package sk.blazicek.udpchat.udp;

import android.os.Handler;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import sk.blazicek.udpchat.Const;
import sk.blazicek.udpchat.MainActivity;
import sk.blazicek.udpchat.chat.Message;
import sk.blazicek.udpchat.udp.task.receive.ProcessConfirmation;
import sk.blazicek.udpchat.udp.task.receive.ProcessDiscovery;
import sk.blazicek.udpchat.udp.task.receive.ProcessMessage;
import sk.blazicek.udpchat.udp.task.receive.ReceiveTask;
import sk.blazicek.udpchat.udp.task.send.Send;

/**
 * Handles received UDP messages
 *
 * @author Jozef Blazicek
 */
public class UDPReceiver extends UDP {
    private ReceiveTask[] tasks = new ReceiveTask[4];
    private DatagramSocket socket;

    private final String deviceIP;


    public UDPReceiver(MainActivity activity, String deviceIP, List<Message> send) {
        super(activity);
        this.deviceIP = deviceIP;

        tasks[0] = new ProcessDiscovery(activity);
        tasks[1] = new ProcessMessage(activity);
        tasks[2] = new ProcessConfirmation(activity,send);
        tasks[3] = tasks[0];

        try {
            socket = new DatagramSocket(Const.INCOMING_PORT);
        } catch (Exception e) {
            activity.finish();
        }

    }

    @Override
    public void start() {
        (new Thread(this)).start();
    }

    @Override
    public void stop() {
        super.stop();
        activity.send(new Send(deviceIP, new byte[4]));
    }

    @Override
    public void run() {
        byte[] data = new byte[2048];

        while (run) {
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);

                // If packet has been successfully received - process
                int length = packet.getLength();
                String ip = packet.getAddress().toString();

                if (ip.contains(deviceIP))
                    continue;

                if (data[0] >= 0 && data[0] <= 3)
                    tasks[data[0]].execute(length, data, ip);
            } catch (Exception e) {
                // Ignore corupted packet
            }
        }
        if (socket != null)
            socket.close();
    }
}
