package sk.blazicek.udpchat.udp.task.send;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import sk.blazicek.udpchat.Const;

/**
 * Sends given data to given IP address
 *
 * @author Jozef Blazicek
 */
public class Send extends SendTask {
    private byte[] data;

    public Send(String ip, byte[] data) {
        this.ip = ip;
        this.data = data;
    }

    @Override
    public void execute(DatagramSocket socket, int size) {
        if (Const.LOG)
            Log.v("Send", "execute(" + new String(data) + ")");

        send(socket, ip, data);
    }


    public static void send(DatagramSocket socket, String ip, byte[] data) {
        if (ip.contains("/"))
            ip = ip.substring(1);
        try {
            InetAddress address = InetAddress.getByName(ip);
            DatagramPacket packet = new DatagramPacket(data, data.length, address, Const.INCOMING_PORT);
            socket.send(packet);
        } catch (Exception e) {
        }
    }
}
