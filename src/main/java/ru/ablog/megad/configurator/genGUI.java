package ru.ablog.megad.configurator;

import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ablog.megad.configurator.windows.MegaConfigScreen;
import ru.ablog.megad.configurator.windows.MegaIpChange;
import ru.ablog.megad.configurator.windows.MegaSelectDeviceScreen;

import java.awt.*;
import java.io.IOException;
import java.util.Collections;

public class genGUI implements OnGUIUpdate {

    public static Window window = new BasicWindow();
    Logger log = LoggerFactory.getLogger(MainWindow.class);
    public static WindowBasedTextGUI textGUI;

    public genGUI() {
        Thread server = new Thread(() -> {
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            terminalFactory.setTerminalEmulatorTitle("MegaDConfig");
            Screen screen = null;
            try {
                screen = terminalFactory.createScreen();
                screen.startScreen();
                MenuBar menubar = new MenuBar();
                Menu menuFile = new Menu("Действие(F2)");

                MenuItem chooseMega = new MenuItem("Выбор меги", () -> {
                    MegaSelectDeviceScreen sel = new MegaSelectDeviceScreen();
                    sel.show();
                    textGUI.setActiveWindow(window);
                });
                //chooseMega.setEnabled(false);

                MenuItem setConfig = new MenuItem("Конфигурация", () -> {
                    MegaConfigScreen cfg = new MegaConfigScreen();
                    cfg.show();
                    textGUI.setActiveWindow(window);
                });

                MenuItem ipChange = new MenuItem("Смена IP", () -> {
                    MegaIpChange ipc = new MegaIpChange();
                    try {
                        ipc.show();
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }
                    textGUI.setActiveWindow(window);
                });
                chooseMega.setEnabled(false);
                ipChange.setEnabled(false);
                setConfig.setEnabled(false);
                menuFile.add(chooseMega);
                menuFile.add(setConfig);
                menuFile.add(ipChange);
                menuFile.add(new MenuItem("Выход", () -> {
                    //log.info("exit");
                    System.exit(0);
                }));
                /*menuFile.add(new MenuItem("Выход", () -> {
                    //log.info("exit");
                    System.exit(0);
                }));*/
                menuFile.setEnabled(false);
                menubar.add(menuFile);
                BasicWindow menuwindow = new BasicWindow();
                menuwindow.setComponent(menubar);

                textGUI = new MultiWindowTextGUI(screen);

                textGUI.addListener((textGUI1, keyStroke) -> {
                    //log.info("key {}", keyStroke);
                    // log.info("key {}", textGUI1.toString());
                    if (keyStroke.getKeyType() == KeyType.F2) {
                        if (MegaConfig.ipDevice != null) {
                            chooseMega.setEnabled(true);
                        }
                        if ((MegaConfig.passDevice != null) && (MegaConfig.ipDevice != null)) {
                            setConfig.setEnabled(true);
                            ipChange.setEnabled(true);

                        }
                        menuFile.setEnabled(true);
                        menuFile.takeFocus();
                        textGUI.setActiveWindow(menuwindow);
                    } else if (keyStroke.getKeyType() == KeyType.Escape) {
                        menuFile.setEnabled(false);
                        textGUI.setActiveWindow(window);
                        //log.info("windows {}", textGUI.getWindows());
                    }
                    return true;
                });

                //window.setHints(Arrays.asList(Window.Hint.CENTERED));
                // Panel mainPanel = new Panel();
                // mainPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
                // window.setComponent(mainPanel);
                window.setHints(Collections.singletonList(Window.Hint.CENTERED));
                //textGUI.setTheme(LanternaThemes.getRegisteredTheme("bigsnake"));
                textGUI.addWindow(menuwindow);
                textGUI.addWindowAndWait(window);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (screen != null) {
                    try {
                        screen.stopScreen();
                        try {
                            MegaConfig.stopUDPServer();
                        } catch (Exception ignored) {
                        }

                    } catch (IOException e) {
                        log.error("{}", e.getMessage());
                    }
                }
            }
        });
        server.start();

    }

    @Override
    public void updateGUI(Component window) {
        //genGUI.window = window;
        genGUI.window.setComponent(window);

    }
}
