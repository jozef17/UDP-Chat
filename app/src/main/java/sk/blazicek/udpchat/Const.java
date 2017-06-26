package sk.blazicek.udpchat;

/**
 * @author Jozef Blazicek
 */
public abstract class Const {
    // Time in milliseconds which thread will wait to re-send message
    public static final int TIMEOUT = 250;
    // Port for receiving messages
    public static final int INCOMING_PORT = 12345;
    // Logging on/off
    public static boolean LOG = false;
}
