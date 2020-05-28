package ru.ablog.megad.configurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

public class MegaSetup implements OnUDPIncomingEventListener{
    private final Logger log = LoggerFactory.getLogger(MegaSetup.class);
    String udpServerAnswer;

    public boolean setNewIP(String oldIp, String NewIp) throws IOException {
        //preamble  mode     pass(sec)     old-ip       new-ip
        // aa 00     04  73 65 63 00 00   c0 a8 00 0e  c0 a8 0a 13
        boolean result = false;
        DatagramSocket socket = new DatagramSocket();
        byte[]buf = {(byte) 170, 0, 12};
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, InetAddress.getByName(MegaConfig.createBroadcastInterface(MegaConfig.getLocaInterfaces().get(3))), 52000);
        socket.send(packet);

        log.info("answer - {}", MegaDUDPRequests.received);
        return result;
    }

    public void onMessageReceive(byte[] result) {

    }


}
