package sk.blazicek.library;

import android.content.Context;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @author Jozef Blazicek
 */
public abstract class FileProcessing {

    /**
     * Reads data from file.
     *
     * @return data from file, If file doesn't exist or error has occurs return null
     */
    public static String load(Context context, String filename) {
        File file = new File(context.getCacheDir(), filename);

        // If file doesn't exist - return null
        if (!file.exists())
            return null;

        FileInputStream fileInputStream = null;
        DataInputStream dataInputStream = null;
        String fromFile = null;

        try {
            fileInputStream = new FileInputStream(file);
            dataInputStream = new DataInputStream(fileInputStream);

            fromFile = dataInputStream.readLine();
        } catch (Exception e) {
        } finally {
            try {
                if (dataInputStream != null)
                    dataInputStream.close();
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (Exception e) {
            }
        }
        return fromFile;
    }

    /**
     * Saves data to file
     */
    public static void save(Context context, String text, String filename) {
        FileOutputStream fileOutputStream = null;
        DataOutputStream dataOutputStream = null;
        File file = new File(context.getCacheDir(), filename);

        try {
            if (!file.exists())
                file.createNewFile();

            fileOutputStream = new FileOutputStream(file);
            dataOutputStream = new DataOutputStream(fileOutputStream);

            byte[] data = text.getBytes();
            dataOutputStream.write(data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataOutputStream != null)
                    dataOutputStream.close();
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (Exception e) {
            }
        }
    }

}
