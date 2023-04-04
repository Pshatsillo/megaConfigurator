package ru.ablog.megad.configurator.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ablog.megad.configurator.interfaces.OnUDPIncomingEventListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class NetworkUtils {
    public static Inet4Address currentNetworkInterface;
    public static String broadcast;
    private OnUDPIncomingEventListener incomingUDP;
    byte[] received;
    Thread server;
    DatagramSocket socket;
    Logger log = LoggerFactory.getLogger(NetworkUtils.class);

    public ArrayList<InetAddress> getLocalInterfaces() {
        ArrayList<InetAddress> listIp = new ArrayList<>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface ip = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddress = ip.getInetAddresses(); enumIpAddress.hasMoreElements(); ) {
                    InetAddress ipv4 = enumIpAddress.nextElement();
                    if (ipv4 instanceof Inet4Address) {
                        listIp.add(ipv4);
                    }
                }
            }
        } catch (SocketException e) {
            log.error("{}", e.getMessage());
        }
        return listIp;
    }

    private void createBroadcastInterface(InetAddress ip) {
        String[] ipArray = ip.getHostAddress().split("[.]");
        broadcast = String.format("%s.%s.%s.255", ipArray[0], ipArray[1], ipArray[2]);
    }

    public void setNetworkInterface(Inet4Address address) {
        currentNetworkInterface = address;
        createBroadcastInterface(address);
    }

    public void startUDPServer(InetAddress inetAddress) {
        final byte[] buf = new byte[5];
        try {
            socket = new DatagramSocket(42000, inetAddress);
            server = new Thread(() -> {
                while (true) {
                    DatagramPacket packet
                            = new DatagramPacket(buf, buf.length);
                    try {
                        socket.receive(packet);
                        received = packet.getData();
                        log.info("packet length {}",packet.getData().length);
                    } catch (IOException ignored) {
                        break;
                    }
                    incomingUDP.onMessageReceive(received);
                }
                socket.close();
            });
            server.start();
        } catch (SocketException e) {
            log.error("{}", e.getMessage());
        }
    }

    public void stopUDPServer() {
        server.interrupt();
        socket.close();
    }

    public void registerUDPEventListener(OnUDPIncomingEventListener incomingUDP) {
        this.incomingUDP = incomingUDP;
    }
}
