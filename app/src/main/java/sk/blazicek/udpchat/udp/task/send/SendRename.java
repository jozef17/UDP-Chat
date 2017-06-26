package sk.blazicek.udpchat.udp.task.send;

import android.util.Log;

import java.net.DatagramSocket;
import java.util.List;

import sk.blazicek.udpchat.Const;
import sk.blazicek.udpchat.MainActivity;

/**
 * Sends new username to active chats
 *
 * @author Jozef Blazicek
 */
public class SendRename extends SendTask {
    private MainActivity activity;
    private String username;

    public SendRename(MainActivity activity, String username) {
        this.username = username;
        this.activity = activity;
    }

    @Override
    public void execute(DatagramSocket socket, int size) {
        if (Const.LOG)
            Log.v("SendRename", "execute");

        List<byte[]> data = breakDown(username, (byte) 3, size, null);
        Object[] addresses = activity.getActiveChats();

        for (Object address : addresses) {
            for (byte[] part : data)
                Send.send(socket, (String) address, part);
        }
    }
}
