package sk.blazicek.udpchat.udp.task.send;

import android.util.Log;

import java.net.DatagramSocket;

import sk.blazicek.library.Convert;
import sk.blazicek.udpchat.Const;

/**
 * @author Jozef Blazicek
 */
public class SendConfirmation extends SendTask {
    private byte[] id;

    public SendConfirmation(String ip, int id) {
        this.ip = ip;
        this.id = Convert.toArray(id);
    }

    @Override
    public void execute(DatagramSocket socket, int size) {
        if (Const.LOG)
            Log.v("SendConfirmation", "execute(" + ip + ")");

        byte[] data = new byte[13];
        data[0] = 2;

        // Message NO 0
        data[1] = 0;
        data[2] = 0;
        data[3] = 0;
        data[4] = 0;

        // Total of 1 message
        data[5] = 1;
        data[6] = 0;
        data[7] = 0;
        data[8] = 0;

        // Metadata
        data[9] = id[0];
        data[10] = id[1];
        data[11] = id[2];
        data[12] = id[3];

        Send.send(socket, ip, data);
    }
}
