package ru.ablog.megad.configurator.windows;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.ComboBox;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ablog.megad.configurator.MainWindow;
import ru.ablog.megad.configurator.MegaConfig;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class MegaStartupScreen {
    Logger log = LoggerFactory.getLogger(MegaStartupScreen.class);
    ArrayList<InetAddress> locaInterfaces;
    public MegaStartupScreen(ArrayList<InetAddress> locaInterfaces) {
        this.locaInterfaces = locaInterfaces;
    }
    public Panel show() {
        final Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        //GridLayout gridLayout = (GridLayout) contentPanel.getLayoutManager();
        //gridLayout.setHorizontalSpacing(2);
        final ComboBox<InetAddress> readOnlyComboBox = new ComboBox<InetAddress>(locaInterfaces);
        readOnlyComboBox.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER));
        readOnlyComboBox.setPreferredSize(new TerminalSize(20, 1));
        contentPanel.addComponent(readOnlyComboBox);
        Button selectInterfaceButton = new Button("Select", new Runnable() {
            @Override
            public void run() {
                //log.info("{}", readOnlyComboBox.getSelectedIndex());
                try {
                    MegaConfig.startUDPserver(locaInterfaces.get(readOnlyComboBox.getSelectedIndex()));
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                contentPanel.removeAllComponents();
                try {
                    MainWindow.showDeviceSelection(locaInterfaces.get(readOnlyComboBox.getSelectedIndex()));
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        contentPanel.addComponent(selectInterfaceButton);

        return contentPanel;
    }
}
