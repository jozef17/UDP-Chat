package sk.blazicek.udpchat.udp;

import android.util.Log;

import java.util.List;

import sk.blazicek.udpchat.Const;
import sk.blazicek.udpchat.MainActivity;
import sk.blazicek.udpchat.chat.Message;
import sk.blazicek.udpchat.udp.task.send.SendMessage;

/**
 * @author Jozef Blazicek
 */
public class UDPTimeout extends UDP {
    List<Message> messages;

    public UDPTimeout(MainActivity activity, List<Message> messages) {
        super(activity);
        this.messages = messages;
    }

    @Override
    public void start() {
        (new Thread(this)).start();
    }

    @Override
    public void run() {
        int count;

        while (run) {
            synchronized (messages) {
                count = messages.size();
            }

            if (Const.LOG)
                Log.v("UDPTimeout", "run " + count);

            // Re sends message
            for (int j = count - 1; j >= 0; j--) {
                SendMessage m = messages.get(j).siConfirmed();

                if (m == null) {
                    synchronized (messages) {
                        messages.remove(j);
                    }
                } else {
                    activity.send(m);
                }
            }

            // Waits givaen time
            synchronized (this) {
                try {
                    wait(Const.TIMEOUT);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
