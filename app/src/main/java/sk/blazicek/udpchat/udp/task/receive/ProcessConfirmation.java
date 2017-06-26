package sk.blazicek.udpchat.udp.task.receive;

import java.util.List;

import sk.blazicek.library.Convert;
import sk.blazicek.udpchat.MainActivity;
import sk.blazicek.udpchat.chat.Message;

/**
 * @author Jozef Blazicek
 */
public class ProcessConfirmation extends ReceiveTask {
    List<Message> send;

    public ProcessConfirmation(MainActivity activity, List<Message> send) {
        super(activity);
        this.send = send;
    }

    @Override
    public void execute(int length, byte[] data, String ip) {
        int id = Convert.toInt(data, 9);

        synchronized (send) {
            for (Message m : send) {
                if (m.getId() == id)
                    m.confirm();
                if (m.getId() > id)
                    break;
            }
        }

    }
}

