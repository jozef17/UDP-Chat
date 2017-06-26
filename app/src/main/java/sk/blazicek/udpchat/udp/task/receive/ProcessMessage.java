package sk.blazicek.udpchat.udp.task.receive;

import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

import sk.blazicek.library.Convert;
import sk.blazicek.udpchat.MainActivity;
import sk.blazicek.udpchat.chat.Chat;
import sk.blazicek.udpchat.chat.Message;
import sk.blazicek.udpchat.udp.task.send.SendConfirmation;

/**
 * TODO
 *
 * @author Jozef Blazicek
 */
public class ProcessMessage extends ReceiveTask {
    private Map<String, Map<Integer, Parts>> processed = new HashMap<String, Map<Integer, Parts>>();

    public ProcessMessage(MainActivity activity) {
        super(activity);
    }

    @Override
    public void execute(int length, byte[] data, String ip) {
        int id = Convert.toInt(data, 9);

        Chat chat = activity.getChat(ip);
        if (chat == null)
            return;

        // Get all received messages from given ip
        Map<Integer, Parts> recceivedParts = processed.get(ip);
        if (recceivedParts == null) {
            recceivedParts = new HashMap<Integer, Parts>();
            processed.put(ip, recceivedParts);
        }

        // Check if message has been processed
        for (Message m : chat.getMessages()) {
            if (m.getId() == id) {
                recceivedParts.remove(ip);
                return;
            }
        }

        // Get all not completes messages from given IP
        Parts parts = recceivedParts.get(id);
        if (parts == null) {
            parts = new Parts();
            recceivedParts.put(id, parts);
        }

        // Process Message
        parts.process(data, 4, length);

        if (!parts.isComplete())
            return;

        // Add new Completed Message
        Message m = new Message(parts.getMessage(), id, activity);
        chat.addMessage(m);

        activity.send(new SendConfirmation(ip, id));
    }
}
