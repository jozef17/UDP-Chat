package sk.blazicek.udpchat.udp.task.send;

import android.util.Log;

import java.net.DatagramSocket;
import java.util.List;

import sk.blazicek.library.Convert;
import sk.blazicek.udpchat.Const;

/**
 * @author Jozef Blazicek
 */
public class SendMessage extends SendTask {
    private static int counter = 0;

    private String message;
    private byte[] id;

    public SendMessage(String ip, String message) {
        this.ip = ip;
        this.message = message;

        id = Convert.toArray(counter);
        counter++;
    }


    @Override
    public void execute(DatagramSocket socket, int size) {
        if (Const.LOG)
            Log.v("SendMessage", "execute(" + message + ")");

        List<byte[]> data = breakDown(message, (byte) 1, size, id);

        for (byte[] part : data) {
            Send.send(socket, ip, part);
        }
    }

    public int getId() {
        return Convert.toInt(id, 0);
    }

    public String getMessage() {
        return message;
    }
}
