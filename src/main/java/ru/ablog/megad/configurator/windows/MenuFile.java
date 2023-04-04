package ru.ablog.megad.configurator.windows;

import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import ru.ablog.megad.configurator.GenGUI;

import static ru.ablog.megad.configurator.GenGUI.textGUI;

public class MenuFile extends Menu{
    MenuItem chooseMega;
    MenuItem setConfig;
    MenuItem ipChange;
    boolean chooseMegaState;
    boolean setConfigState = false;
    boolean ipChangeState = false;
    public MenuFile() {
        super("Действие(F2)");
    }

    public Menu getMenu(){
        chooseMega = new MenuItem("Выбор меги", () -> {
            MainWindow window = new MainWindow();
            window.show();
            this.setEnabled(false);
            textGUI.setActiveWindow(GenGUI.window);
        });
        setConfig = new MenuItem("Конфигурация", () -> {
        });
        ipChange = new MenuItem("Смена IP", () -> {
            MegaChangeIp changeIp = new MegaChangeIp();
            changeIp.show();
            this.setEnabled(false);
            textGUI.setActiveWindow(GenGUI.window);
        });
        chooseMega.setEnabled(chooseMegaState);
        ipChange.setEnabled(ipChangeState);
        setConfig.setEnabled(setConfigState);
        this.add(chooseMega);
        this.add(setConfig);
        this.add(ipChange);
        this.add(new MenuItem("Выход", () -> System.exit(0)));
        this.setEnabled(false);
        return this;
    }

    public void setIpMenu(boolean status) {
        ipChange.setEnabled(status);
    }

    public void setMegaMenu(boolean status) {
        chooseMega.setEnabled(status);
    }

    public void setConfigMenu(boolean status) {
        setConfig.setEnabled(status);
    }
}
