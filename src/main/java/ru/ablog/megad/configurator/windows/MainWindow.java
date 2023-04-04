package ru.ablog.megad.configurator.windows;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.ComboBox;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ablog.megad.configurator.GenGUI;
import ru.ablog.megad.configurator.interfaces.OnUDPIncomingEventListener;
import ru.ablog.megad.configurator.utils.NetworkUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;

public class MainWindow implements OnUDPIncomingEventListener {
    Logger log = LoggerFactory.getLogger(MainWindow.class);
    NetworkUtils netUtils = new NetworkUtils();
    ArrayList<String> ip_list = new ArrayList<>();

    public Component create(ArrayList<InetAddress> localInterfaces) {
        OnUDPIncomingEventListener broadcastListener = this;
        netUtils.registerUDPEventListener(broadcastListener);
        final Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        final ComboBox<InetAddress> readOnlyComboBox = new ComboBox<>(localInterfaces);
        readOnlyComboBox.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER));
        readOnlyComboBox.setPreferredSize(new TerminalSize(20, 1));
        contentPanel.addComponent(readOnlyComboBox);

        Button selectInterfaceButton = new Button("Select", () -> {
            log.info("{}", readOnlyComboBox.getText());
            netUtils.startUDPServer(localInterfaces.get(readOnlyComboBox.getSelectedIndex()));
            netUtils.setNetworkInterface((Inet4Address) localInterfaces.get(readOnlyComboBox.getSelectedIndex()));
            contentPanel.removeAllComponents();
            try (DatagramSocket socket = new DatagramSocket()) {
                byte[] buf = {(byte) 170, 0, 12, (byte) 218, (byte) 202};
                DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(NetworkUtils.broadcast), 52000);
                socket.send(packet);
                Thread.sleep(200);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            netUtils.stopUDPServer();
            //Next screen
            MegaSelectDeviceWindow selectMega = new MegaSelectDeviceWindow();
            selectMega.show(ip_list);
        });
        contentPanel.addComponent(selectInterfaceButton);
        return contentPanel;
    }

    public void onMessageReceive(byte[] result) {
        String ips = String.format("%d.%d.%d.%d", result[1] & 0xFF, result[2] & 0xFF, result[3] & 0xFF, result[4] & 0xFF);
        ip_list.add(ips);
    }


    public void show() {
        GenGUI.window.setComponent(create(netUtils.getLocalInterfaces()));
    }
}
