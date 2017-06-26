package sk.blazicek.udpchat.udp.task.send;

import android.util.Log;

import java.net.DatagramSocket;
import java.util.List;

import sk.blazicek.udpchat.Const;

/**
 * @author Jozef Blazicek
 */
public class SendResponse extends SendTask {
    private String message;
    private byte type;
    private byte[] metadata;

    public SendResponse(String ip, byte type, String message, byte[] metadata) {
        this.ip = ip;
        this.message = message;
        this.type = type;
        this.metadata = metadata;
    }


    @Override
    public void execute(DatagramSocket socket, int size) {
        if (Const.LOG)
            Log.v("SendResponse", "execute(" + message + ")");

        List<byte[]> data = breakDown(message, type, size, metadata);

        for (byte[] part : data)
            Send.send(socket, ip, part);
    }
}
