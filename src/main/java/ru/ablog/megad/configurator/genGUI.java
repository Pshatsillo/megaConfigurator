package ru.ablog.megad.configurator;

import com.googlecode.lanterna.gui2.*;
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
                //startupScreen = new MegaStartupScreen();
                Screen screen = null;
                try {
                    screen = terminalFactory.createScreen();
                    screen.startScreen();

                    WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
                    //window.setHints(Arrays.asList(Window.Hint.CENTERED));
                    // Panel mainPanel = new Panel();
                    // mainPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
                    // window.setComponent(mainPanel);
                    window.setHints(Arrays.asList(Window.Hint.CENTERED));
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
