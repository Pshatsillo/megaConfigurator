package ru.ablog.megad.configurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import static java.lang.Thread.sleep;

public class MegaDUDPRequests {
    private final Logger log = LoggerFactory.getLogger(MegaDUDPRequests.class);
    private OnUDPIncomingEventListener incomingUDP;

    private final DatagramSocket socket;
    private final byte[]buf = new byte[5];
    public static byte[] received;

    public void registerOnGeekEventListener(OnUDPIncomingEventListener incomingUDP)
    {
        this.incomingUDP = incomingUDP;
    }

    public MegaDUDPRequests(InetAddress address) throws SocketException {
        socket = new DatagramSocket(42000, address);
        //socket.setSoTimeout(10000);
    }

    Thread server = new Thread(new Runnable() {
        public void run() {
            boolean running = true;
            while (true) {
                DatagramPacket packet
                        = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                } catch (IOException ignored) {
                }
                received = packet.getData();

                try {
                    sleep(1);
                }
                catch (InterruptedException e) {
                    break;
                }
                incomingUDP.onMessageReceive(received);
            }
            socket.close();
        }
    });

    public void startUDPServer()
    {
        server.start();
    }

    public void stop() throws SocketException {
        server.interrupt();
        socket.close();
    }
}
