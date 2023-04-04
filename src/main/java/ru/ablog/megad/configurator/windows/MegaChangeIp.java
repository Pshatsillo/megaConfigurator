package ru.ablog.megad.configurator.windows;

import com.googlecode.lanterna.gui2.AbsoluteLayout;
import com.googlecode.lanterna.gui2.Panel;
import ru.ablog.megad.configurator.GenGUI;

public class MegaChangeIp {
    public void show() {
        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new AbsoluteLayout());
        GenGUI.window.setComponent(mainPanel);
    }
}
