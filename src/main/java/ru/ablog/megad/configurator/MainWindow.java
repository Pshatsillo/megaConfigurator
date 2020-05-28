package ru.ablog.megad.configurator;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

import com.googlecode.lanterna.gui2.*;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import ru.ablog.megad.configurator.windows.MegaMainScreen;
import ru.ablog.megad.configurator.windows.MegaSelectDeviceScreen;
import ru.ablog.megad.configurator.windows.MegaStartupScreen;

public class MainWindow implements OnUDPIncomingEventListener {
	Logger log = LoggerFactory.getLogger(MainWindow.class);
	//List<String> ips;
	Window window = new BasicWindow();

	static MegaMainScreen mainScreen;
	static MegaSelectDeviceScreen megaSelectDeviceScreen;
	static WindowBasedTextGUI textGUI;


	public Component create(ArrayList<InetAddress> locaInterfaces) {
		MegaStartupScreen startupScreen = new MegaStartupScreen(locaInterfaces);
		//Panel contentPanel = new Panel();
		//contentPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
		Component contentPanel = startupScreen.show();
		/*DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
		startupScreen = new MegaStartupScreen(locaInterfaces);
		Screen screen = null;
		try {
            screen = terminalFactory.createScreen();
            screen.startScreen();
            
            textGUI = new MultiWindowTextGUI(screen);
			window.setHints(Arrays.asList(Window.Hint.CENTERED));
			Panel mainPanel = new Panel();
			mainPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));


			//window.setComponent(mainPanel.withBorder(Borders.singleLine("Main Panel")));
			//textGUI.addWindow(window);
			//

			//contentPanel.addComponent(new Button("Button", () -> MessageDialog.showMessageDialog(textGUI, "MessageBox", "This is a message box", MessageDialogButton.OK)).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER)));

*//*        	Button button = new Button("Enter");
            
            Panel contentPanel = new Panel();
            contentPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        	Panel leftPanel = new Panel();
        	leftPanel.addComponent(button);
        	contentPanel.addComponent(leftPanel.withBorder(Borders.singleLine("Left Panel")));

        	
        	Panel rightPanel = new Panel();
        	rightPanel.addComponent(new Label("Forename"));
        	rightPanel.addComponent(new TextBox());
        	contentPanel.addComponent(rightPanel.withBorder(Borders.singleLine("Right Panel")));
        	
        	contentPanel.addComponent(new EmptySpace(new TerminalSize(0,0))); // Empty space underneath labels
        	contentPanel.addComponent(new Button("Submit"));
        	//contentPanel.addComponent(button);
            //contentPanel.addComponent(new Button("Enter"));

            window.setComponent(contentPanel.withBorder(Borders.doubleLine("Main window")));
            *//*
			//window.setComponent(mainPanel);
            //window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));
			//startup();
			//window.setComponent(startupScreen.show().);
            //textGUI.addWindowAndWait(window);
		}catch (Exception e) {
			log.error("error: {}", e);
		}finally {
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
        }*/
		return contentPanel;
	}

	private void startup() {
		//window.setComponent(startupScreen.show().withBorder(Borders.singleLine("Select Megad network interface")));
	}

	public static void showDeviceSelection(InetAddress inetAddress) throws IOException, InterruptedException {
		megaSelectDeviceScreen = new MegaSelectDeviceScreen(inetAddress);
		MegaConfig.gm.refreshWindow(megaSelectDeviceScreen.show().withBorder(Borders.singleLine("Select device")));
		//window.setComponent();
	}

	public static void showMainScreen(String inetAddress) throws IOException, InterruptedException {
		mainScreen = new MegaMainScreen(inetAddress, textGUI);
		MegaConfig.gm.refreshWindow(mainScreen.show().withBorder(Borders.singleLine("Main Panel")));
	}

	public void onMessageReceive(byte[] result) {
		//log.warn("receive from MainWindow: {}", result);
		//ips.add(result);
	}


}
