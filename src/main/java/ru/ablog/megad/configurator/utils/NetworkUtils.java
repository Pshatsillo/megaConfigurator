package ru.ablog.megad.configurator.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class NetworkUtils {

    public static ArrayList<InetAddress> getLocaInterfaces() throws SocketException {
        ArrayList<InetAddress> listIp = new ArrayList<>();
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface ip = en.nextElement();
            for (Enumeration<InetAddress> enumIpAddr = ip.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                InetAddress ipv4 = enumIpAddr.nextElement();
                if (ipv4 instanceof Inet4Address) {
                    listIp.add(ipv4);
                }
            }
        }
        return listIp;
    }

    public static String createBroadcastInterface(InetAddress ip) {
        String[] ipArray = ip.getHostAddress().split("[.]");
        return String.format("%s.%s.%s.255", ipArray[0], ipArray[1], ipArray[2]);
    }

}
