package sk.blazicek.udpchat.udp.task.send;

import android.content.Context;
import android.util.Log;

import java.net.DatagramSocket;
import java.util.List;

import sk.blazicek.library.Network;
import sk.blazicek.udpchat.Const;

/**
 * Broadcasts discovery
 *
 * @author Jozef Blazicek
 */
public class Broadcast extends SendTask {
    private String username;
    private Context context;

    public Broadcast(String username, Context context) {
        this.username = username;
        this.context = context;
    }

    @Override
    public void execute(DatagramSocket socket, int size) {
        if (Const.LOG)
            Log.v("Broadcast", "execute");

        List<byte[]> data = breakDown(username, (byte) 0, size, new byte[]{0});

        // Gets IP range
        int minMaxIP[] = Network.minMaxIp(context);

        // Sends data to generated range of addresses
        for (int j = minMaxIP[0] + 1; j < minMaxIP[1]; j++) {
            String ip = ((j & 0xFF000000) >>> 24) + "." + ((j & 0xFF0000) >>> 16) + "." + ((j & 0xFF00) >>> 8) + "." + (j & 0xFF);

            for (byte[] part : data) {
                Send.send(socket, ip, part);
            }
        }
    }
}
