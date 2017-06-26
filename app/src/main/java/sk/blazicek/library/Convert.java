package sk.blazicek.library;

/**
 * @author Jozef Blazicek
 */
public abstract class Convert {

    /**
     * Converts given number into byte array of size 4
     */
    public static byte[] toArray(int j) {
        byte[] array = new byte[4];
        array[0] = (byte) (j & 0xff);
        array[1] = (byte) ((j >> 8) & 0xff);
        array[2] = (byte) ((j >> 16) & 0xff);
        array[3] = (byte) ((j >> 24) & 0xff);

        return array;
    }

    /**
     * Converts byte Array to int
     */
    public static int toInt(byte[] array, int offset) {
        int a = (array[offset] & 0xff);
        int b = (array[offset + 1] << 8);
        int c = (array[offset + 2] << 16);
        int d = (array[offset + 3] << 24);
        return a | b | c | d;
    }
}
