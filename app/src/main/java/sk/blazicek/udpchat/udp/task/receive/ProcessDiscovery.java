package sk.blazicek.udpchat.udp.task.receive;

import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

import sk.blazicek.udpchat.MainActivity;
import sk.blazicek.udpchat.chat.Chat;
import sk.blazicek.udpchat.udp.task.send.SendResponse;

/**
 * @author Jozef Blazicek
 */
public class ProcessDiscovery extends ReceiveTask {
    private Map<String, Parts> discovery = new HashMap<>();
    private Map<String, Parts> username = new HashMap<>();


    public ProcessDiscovery(MainActivity activity) {
        super(activity);
    }

    /**
     * Remove uncompleted messages after time exceeded
     */
    private void applyTimeout() {
        Object[] addresses = discovery.keySet().toArray();
        for (Object o : addresses) {
            if (discovery.get(o).getTimeLeft() < 0)
                discovery.remove(o);
        }

        addresses = username.keySet().toArray();
        for (Object o : addresses) {
            if (username.get(o).getTimeLeft() < 0)
                username.remove(o);
        }
    }

    @Override
    public void execute(int length, byte[] data, String ip) {
        applyTimeout();

        Parts parts = null;
        int metadataSize = 0;

        if (data[0] == 0) {
            parts = discovery.get(ip);
            metadataSize = 1;
        } else if (data[0] == 3) {
            parts = username.get(ip);
        }

        if (parts == null) {
            parts = new Parts();
            if (data[0] == 0) {
                discovery.put(ip, parts);
            } else if (data[0] == 3) {
                username.put(ip, parts);
            }
        }

        parts.process(data, metadataSize, length);
        if (!parts.isComplete())
            return;

        // Process completed message
        if (data[0] == 0) {
            discovery.remove(ip);
        } else if (data[0] == 3) {
            username.remove(ip);
        }

        Chat chat = activity.getChat(ip);
        String username = parts.getMessage();

        if (chat == null) {
            chat = new Chat(ip, username, activity);
            activity.registerChat(chat);
        } else {
            chat.changeUsername(username);
        }

        if (data[0] == 0 && data[1] == 0) {
            activity.send(new SendResponse(ip, (byte) 0, activity.getUsername(), new byte[]{1}));
        }

        Handler handler = new Handler(activity.getMainLooper());
        handler.post(activity);
    }
}

