package ru.ablog.megad.configurator.windows;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.ComboBox;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ablog.megad.configurator.GenGUI;

import java.util.ArrayList;

public class MegaSelectDeviceWindow {
    Logger log = LoggerFactory.getLogger(MegaSelectDeviceWindow.class);
    public MegaSelectDeviceWindow() {
    }

    public void show(ArrayList<String> iplist) {
        Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        ComboBox<String> readOnlyComboBox = new ComboBox<>(iplist);
        readOnlyComboBox.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER));
        readOnlyComboBox.setPreferredSize(new TerminalSize(20, 1));
        contentPanel.addComponent(readOnlyComboBox);
        Button selectInterfaceButton = new Button("Select", () -> {
            log.info("Select pressed, {} selected", readOnlyComboBox.getSelectedItem());
            MegaLoginScreen selectMainScreen = new MegaLoginScreen(readOnlyComboBox.getSelectedItem());
            selectMainScreen.show();
        });
        contentPanel.addComponent(selectInterfaceButton);
        GenGUI.window.setComponent(contentPanel.withBorder((Borders.singleLine("Select device"))));
    }
}
