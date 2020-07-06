package ru.ablog.megad.configurator.windows;

import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Panel;
import ru.ablog.megad.configurator.genGUI;

public class MegaConfigScreen {
    public void show() {
        Panel panel = new Panel();
        genGUI.window.setComponent(panel.withBorder(Borders.doubleLine("Конфигурация")));
    }
}
