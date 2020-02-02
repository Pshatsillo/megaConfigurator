package src.main;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

public class MainWindow {

	Logger log = LoggerFactory.getLogger(MainWindow.class);
	public void addFirmware(String firmware) {
			
	}

	public void create() {
		DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
		Screen screen = null;
		try {
            screen = terminalFactory.createScreen();
            screen.startScreen();
            
            final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
            final Window window = new BasicWindow("MegaD Config util");
        	Button button = new Button("Enter");
            
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
            window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));
            textGUI.addWindowAndWait(window);
		}catch (Exception e) {
			log.error("error: {}", e);
		}finally {
            if(screen != null) {
                try {
                    screen.stopScreen();
                }
                catch(IOException e) {
                   log.error("{}", e);
                }
            }
        }
	}

}
