package sk.blazicek.udpchat.udp.task.send;

import android.util.Log;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import sk.blazicek.library.Convert;
import sk.blazicek.udpchat.Const;

/**
 * Task executed by UDP Sender
 *
 * @author Jozef Blazicek
 */
public abstract class SendTask {
    protected String ip;

    /**
     * Divides message into parts, adds header and metadata
     */
    protected List<byte[]> breakDown(String message, byte type, int totalSize, byte[] metadata) {
        if (Const.LOG)
            Log.v("SendTask", "breakDown(" + message + ")");

        byte[] data = message.getBytes();
        int dataSize = totalSize - 9;
        int headerSize = 9;

        if (metadata != null) {
            dataSize -= metadata.length;
            headerSize += metadata.length;
        } else {
            metadata = new byte[0];
        }

        List<byte[]> divided = new ArrayList<byte[]>();
        int numParts = (data.length + dataSize - 1) / dataSize;
        byte[] numPartsArray = Convert.toArray(numParts);

        for (int j = 0; j < numParts; j++) {
            int first = j * dataSize;
            int last = (j + 1) * dataSize;
            if (last >= data.length)
                last = data.length;
            int partSize = last - first;

            byte part[] = new byte[headerSize + partSize];
            byte id[] = Convert.toArray(j);
            // Added type byte
            part[0] = type;

            // Added id and count fields
            for (int b = 0; b < 4; b++) {
                part[b + 1] = id[b];
                part[b + 5] = numPartsArray[b];
            }
            // Added Metadata
            for (int b = 9; b < headerSize; b++) {
                part[b] = metadata[b - 9];
            }
            // Added Message Data
            for (int b = 0; b < partSize; b++) {
                part[b + headerSize] = data[first + b];
            }
            divided.add(part);
        }
        return divided;
    }

    public abstract void execute(DatagramSocket socket, int size);
}
