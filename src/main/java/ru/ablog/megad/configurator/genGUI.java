package ru.ablog.megad.configurator;

import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ablog.megad.configurator.windows.MegaStartupScreen;

import java.io.IOException;
import java.util.Arrays;

public class genGUI implements OnGUIUpdate {

    //List<String> ips;
    static Window window = new BasicWindow();
    //static WindowBasedTextGUI textGUI;
    Logger log = LoggerFactory.getLogger(MainWindow.class);
    MegaStartupScreen startupScreen;

    public genGUI() throws IOException {
        Thread server = new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
                terminalFactory.setTerminalEmulatorTitle("MegaDConfig");
                Screen screen = null;
                try {
                    screen = terminalFactory.createScreen();
                    screen.startScreen();
                    MenuBar menubar = new MenuBar();
                    Menu menuFile = new Menu("Действие(F2)");
                    menuFile.add(new MenuItem("Выход", () -> {log.info("exit");  System.exit(0);}));
                    menuFile.setEnabled(false);
                    menubar.add(menuFile);
                    BasicWindow menuwindow = new BasicWindow();
                    menuwindow.setComponent(menubar);

                    WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

                    textGUI.addListener((textGUI1, keyStroke) -> {
                        //log.info("key {}", keyStroke);
                       // log.info("key {}", textGUI1.toString());
                        if(keyStroke.getKeyType() == KeyType.F2){
                            menuFile.setEnabled(true);
                            menuFile.takeFocus();
                            textGUI.setActiveWindow(menuwindow);
                        } else if(keyStroke.getKeyType() == KeyType.Escape){
                            menuFile.setEnabled(false);
                            //textGUI.setActiveWindow(window);
                            //log.info("windows {}", textGUI.getWindows());
                        }
                        return true;
                    });

                    //window.setHints(Arrays.asList(Window.Hint.CENTERED));
                    // Panel mainPanel = new Panel();
                    // mainPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
                    // window.setComponent(mainPanel);
                    window.setHints(Arrays.asList(Window.Hint.CENTERED));
                    //textGUI.setTheme(LanternaThemes.getRegisteredTheme("bigsnake"));
                    textGUI.addWindow(menuwindow);
                    textGUI.addWindowAndWait(window);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                if(screen != null) {
                    try {
                        screen.stopScreen();
                        try {
                            MegaConfig.stopUDPServer();
                        } catch (Exception ignored){
                        }

                    }
                    catch(IOException e) {
                        log.error("{}", e);
                    }
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
