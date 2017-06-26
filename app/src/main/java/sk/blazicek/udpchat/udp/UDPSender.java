package sk.blazicek.udpchat.udp;

import android.widget.Toast;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import sk.blazicek.udpchat.MainActivity;
import sk.blazicek.udpchat.R;
import sk.blazicek.udpchat.udp.task.send.SendTask;

/**
 * Handles sending UDP messages
 *
 * @author Jozef Blazicek
 */
public class UDPSender extends UDP {
    private List<SendTask> tasks = new ArrayList<SendTask>();
    private DatagramSocket socket;
    private int maxSize = 512;

    public UDPSender(MainActivity activity) {
        super(activity);

        try {
            socket = new DatagramSocket();
        } catch (Exception e) {
            activity.finish();
        }
    }

    @Override
    public void start() {
        (new Thread(this)).start();
    }

    @Override
    public void stop() {
        super.stop();

        synchronized (this) {
            this.notify();
        }
    }

    @Override
    public void run() {
        SendTask current;

        while (run || tasks.size() != 0) {

            // Wait while tasks list is empty
            while (run && tasks.size() == 0) {
                try {
                    synchronized (this) {
                        wait();
                    }
                } catch (InterruptedException e) {
                }
            }

            // break if stop and all tasks are done
            if (!run && tasks.size() == 0)
                break;

            // Get first task and execute
            synchronized (tasks) {
                current = tasks.get(0);
                tasks.remove(0);
            }

            synchronized (socket) {
                current.execute(socket, maxSize);
            }
        }
    }

    /**
     * Adds new task to task list and notifies thread
     */
    public void addTask(SendTask task) {
        synchronized (task) {
            tasks.add(task);
        }
        synchronized (this) {
            this.notify();
        }
    }

    public int getPort() {
        synchronized (socket) {
            return socket.getLocalPort();
        }
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        if (maxSize < 14 || maxSize > 2000) {
            Toast.makeText(activity, activity.getResources().getString(R.string.error_size), Toast.LENGTH_LONG).show();
            this.maxSize = 512;
            return;
        }
        this.maxSize = maxSize;
    }

    public void setPort(int port) {
        synchronized (socket) {
            socket.close();
            try {
                socket = new DatagramSocket(port);
            } catch (Exception e) {
                Toast.makeText(activity, activity.getResources().getString(R.string.error_port), Toast.LENGTH_LONG).show();
                try {
                    socket = new DatagramSocket();
                } catch (Exception ee) {
                    activity.finish();
                }
            }
        }
    }
}
