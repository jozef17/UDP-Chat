package sk.blazicek.udpchat.udp;

import sk.blazicek.udpchat.MainActivity;

/**
 * @author Jozef Blazicek
 */
public abstract class UDP implements Runnable {
    protected MainActivity activity;
    protected boolean run = true;

    public UDP(MainActivity activity) {
        this.activity = activity;
    }

    /**
     * Creates and starts new thread
     */
    public abstract void start();

    /**
     * Stops execution of thread, firstly finishes current tasks if necessery
     */
    public void stop() {
        run = false;
    }
}
