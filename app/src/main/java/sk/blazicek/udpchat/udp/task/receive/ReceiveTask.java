package sk.blazicek.udpchat.udp.task.receive;

import sk.blazicek.library.Convert;
import sk.blazicek.udpchat.MainActivity;

/**
 * Task executed by UDPReceiver
 *
 * @author Jozef Blazicek
 */
public abstract class ReceiveTask {
    protected MainActivity activity;

    protected class Parts {
        private String[] messages;
        private int left;
        private long timeLeft = 500;
        private long last;

        Parts() {
            last = System.currentTimeMillis();
        }

        void process(byte data[], int metadataSize, int length) {
            timeLeft = 500;
            int id = Convert.toInt(data, 1);
            int count = Convert.toInt(data, 5);

            if (messages == null) {
                messages = new String[count];
                left = count;

                for (int j = 0; j < count; j++)
                    messages[j] = null;
            }

            if (messages[id] != null)
                return;

            left--;
            messages[id] = new String(data).substring(8 + metadataSize, length);
        }

        boolean isComplete() {
            return left == 0;
        }

        String getMessage() {
            StringBuffer sb = new StringBuffer();
            for (String part : messages)
                sb.append(part);

            return sb.toString();
        }

        long getTimeLeft() {
            long tmp = System.currentTimeMillis();
            long delta = tmp - last;
            last = tmp;
            timeLeft -= delta;
            return timeLeft;
        }
    }

    public ReceiveTask(MainActivity activity) {
        this.activity = activity;
    }

    public abstract void execute(int length, byte data[], String ip);
}
