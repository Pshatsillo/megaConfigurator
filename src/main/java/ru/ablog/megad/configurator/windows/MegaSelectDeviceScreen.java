package ru.ablog.megad.configurator.windows;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import ru.ablog.megad.configurator.MainWindow;
import ru.ablog.megad.configurator.MegaConfig;
import ru.ablog.megad.configurator.OnUDPIncomingEventListener;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class MegaSelectDeviceScreen implements OnUDPIncomingEventListener {
    ArrayList<String> iplist = new ArrayList<String>();
    InetAddress inetAddress;
    public MegaSelectDeviceScreen(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
        MegaConfig.registerDeviceListener(this);
    }

    public MegaSelectDeviceScreen() {

    }


    ArrayList<String> requestMegaList() throws IOException, InterruptedException {
        DatagramSocket socket;
        InetAddress address;

        socket = new DatagramSocket();
        byte[] buf = {(byte) 170, 0, 12};
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, InetAddress.getByName(MegaConfig.createBroadcastInterface(inetAddress)), 52000);
        socket.send(packet);
        Thread.sleep(1000);
        return iplist;
    }
    public Component show() throws IOException, InterruptedException {
        Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        //GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
        //gridLayout.setHorizontalSpacing(2);
        ComboBox<String> readOnlyComboBox = new ComboBox<String>(requestMegaList());
        readOnlyComboBox.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER));
        readOnlyComboBox.setPreferredSize(new TerminalSize(20, 1));
        contentPanel.addComponent(readOnlyComboBox);
        Button selectInterfaceButton = new Button("Select", new Runnable() {
            @Override
            public void run() {
                try {
                    MainWindow.showMainScreen(readOnlyComboBox.getSelectedItem());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        contentPanel.addComponent(selectInterfaceButton);
        return contentPanel;
    }

    @Override
    public void onMessageReceive(byte[] result) {
        String ips = String.format("%d.%d.%d.%d", result[1] & 0xFF, result[2] & 0xFF, result[3] & 0xFF, result[4] & 0xFF);
        iplist.add(ips);
    }
}
