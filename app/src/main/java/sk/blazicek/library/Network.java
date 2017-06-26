package sk.blazicek.library;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import java.nio.ByteOrder;

/**
 * @author Jozef Blazicek
 */
public abstract class Network {

    public static String getDeviceIP(Context context) {
        DhcpInfo dhcpInfo = ((WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getDhcpInfo();
        int deviceIp = dhcpInfo.ipAddress;

        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN))
            deviceIp = Integer.reverseBytes(deviceIp);

        // Convert IP to String
        return ((deviceIp & 0xFF000000) >>> 24) + "." + ((deviceIp & 0xFF0000) >>> 16) + "." + ((deviceIp & 0xFF00) >>> 8) + "." + (deviceIp & 0xFF);
    }

    /**
     * Generates range of ip addresses for local networ broadcast
     *
     * @return int array of size 2. first element is minIp, second is maxIP
     */
    public static int[] minMaxIp(Context context) {
        // Generates range of ip addresses for broadcasting request
        DhcpInfo dhcpInfo = ((WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getDhcpInfo();

        int minIp, maxIp;
        int deviceIp = dhcpInfo.ipAddress;
        int mask = dhcpInfo.netmask;
        int maskCode = 1;

        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            deviceIp = Integer.reverseBytes(deviceIp);
            mask = Integer.reverseBytes(mask);
        }

        // Counts subnet mask bytes
        for (; maskCode <= 4 * 8; maskCode++) {
            if (((mask >> maskCode) & 1) == 1)
                break;
        }

        minIp = deviceIp & mask;
        maxIp = (Integer.MAX_VALUE >> (8 * 4 - maskCode - 1)) | minIp;

        int minMaxIp[] = new int[2];
        minMaxIp[0] = minIp;
        minMaxIp[1] = maxIp;
        return minMaxIp;
    }
}
