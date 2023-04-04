package ru.ablog.megad.configurator;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ablog.megad.configurator.interfaces.OnGUIUpdate;
import ru.ablog.megad.configurator.windows.MenuFile;

import java.io.IOException;
import java.util.Collections;

public class GenGUI implements OnGUIUpdate {

    public static Window window = new BasicWindow();
    Logger log = LoggerFactory.getLogger(GenGUI.class);
    public static WindowBasedTextGUI textGUI;
    public static boolean isMegaSelected = false;
    public static boolean isPasswordCorrect = false;

    public GenGUI() {
        Thread server = new Thread(() -> {
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            terminalFactory.setTerminalEmulatorTitle("MegaDConfig");
            try {
                Screen screen = terminalFactory.createScreen();
                screen.startScreen();
                MenuBar menubar = new MenuBar();
                MenuFile menuFile = new MenuFile();
                menubar.add(menuFile.getMenu());
                BasicWindow menuWindow = new BasicWindow();
                menuWindow.setComponent(menubar);

                textGUI = new MultiWindowTextGUI(screen);

                //Creating key press detect
                textGUI.addListener((textGUI1, keyStroke) -> {
                    if (keyStroke.getKeyType() == KeyType.F2) {
                        if (isMegaSelected) {
                            menuFile.setIpMenu(true);
                            menuFile.setMegaMenu(true);
                            log.info("mega selected");
                        }
                        if(isPasswordCorrect){
                            menuFile.setConfigMenu(true);
                        }
                        menuFile.setEnabled(true);
                        menuFile.takeFocus();
                        textGUI.setActiveWindow(menuWindow);
                    } else if (keyStroke.getKeyType() == KeyType.Escape) {
                        menuFile.setEnabled(false);
                        textGUI.setActiveWindow(window);
                    }
                    return true;
                });
                window.setHints(Collections.singletonList(Window.Hint.CENTERED));
                textGUI.addWindow(menuWindow);
                textGUI.addWindowAndWait(window);
            } catch (IOException e) {
                log.error("Error {}", e.getMessage());
            }
        });
        server.start();
    }

    @Override
    public void updateGUI(Component window) {
        GenGUI.window.setComponent(window);
    }
}
