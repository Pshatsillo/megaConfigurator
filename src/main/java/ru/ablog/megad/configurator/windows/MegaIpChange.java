package ru.ablog.megad.configurator.windows;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.*;
import ru.ablog.megad.configurator.MegaConfig;
import ru.ablog.megad.configurator.genGUI;

import java.awt.*;
import java.awt.event.KeyEvent;

public class MegaIpChange {
    Panel mainPanel;

    public Component show() throws AWTException {
        mainPanel = new Panel();
        genGUI.window.setComponent(mainPanel.withBorder(Borders.doubleLine("Смена IP")));
        mainPanel.setLayoutManager(new AbsoluteLayout());

        Label oldiplabel = new Label("Старый IP: ");
        oldiplabel.setPosition(new TerminalPosition(1, 0));
        oldiplabel.setSize(new TerminalSize(11, 1));

        Label from = new Label(MegaConfig.ipDevice);
        from.setPosition(new TerminalPosition(12, 0));
        from.setSize(new TerminalSize(16, 1));

        Label newiplabel = new Label("Новый IP : ");
        newiplabel.setPosition(new TerminalPosition(1, 1));
        newiplabel.setSize(new TerminalSize(11, 1));

        TextBox newip = new TextBox();
        //password.setText("sec");
        newip.setPosition(new TerminalPosition(12, 1));
        newip.setSize(new TerminalSize(16, 1));
        mainPanel.nextFocus(newip);
        Robot robot = new Robot();

        robot.keyPress(KeyEvent.VK_DOWN);
        robot.keyRelease(KeyEvent.VK_DOWN);

        mainPanel.addComponent(oldiplabel);
        mainPanel.addComponent(from);
        mainPanel.addComponent(newiplabel);
        mainPanel.addComponent(newip);
        return mainPanel;
    }
}
