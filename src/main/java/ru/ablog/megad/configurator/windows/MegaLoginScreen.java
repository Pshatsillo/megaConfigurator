package ru.ablog.megad.configurator.windows;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.AbsoluteLayout;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ablog.megad.configurator.GenGUI;
import ru.ablog.megad.configurator.MegaConfig;
import ru.ablog.megad.configurator.utils.MegaHTTPConnect;

import java.io.IOException;

import static ru.ablog.megad.configurator.GenGUI.textGUI;

public class MegaLoginScreen {
    Logger log = LoggerFactory.getLogger(MegaLoginScreen.class);
    String ipAddress;

    public void show() {
        GenGUI.isMegaSelected = true;
        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new AbsoluteLayout());

        Label label = new Label("Enter password: ");
        label.setPosition(new TerminalPosition(1, 0));
        label.setSize(new TerminalSize(17, 1));

        TextBox password = new TextBox();
        password.setText("sec");
        password.setPosition(new TerminalPosition(17, 0));
        password.setSize(new TerminalSize(8, 1));

        Button enter = new Button("Enter");
        enter.addListener((button -> waitscreen(password)));
        enter.setPosition(new TerminalPosition(17, 1));
        enter.setSize(new TerminalSize(7, 1));

        mainPanel.addComponent(label);
        mainPanel.addComponent(password);
        mainPanel.addComponent(enter);
        GenGUI.window.setComponent(mainPanel);
    }

    public MegaLoginScreen(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    private void waitscreen(TextBox password) {
        MegaConfig.currentIp = ipAddress;
        if (password.getText().length() > 5) {
            MessageDialog.showMessageDialog(textGUI, "Error", "Password must be 5 symbols maximum");
        } else {
            MegaHTTPConnect http = new MegaHTTPConnect();
            try {
                String pas = http.connectToMega("http://" + ipAddress + "/" + password.getText());
                if (pas.equals("Unauthorized")) {
                    MessageDialog.showMessageDialog(textGUI, "ОШИБКА", "Wrong password");
                } else {
                    log.info("{}", pas);
                }
            } catch (IOException e) {
                MessageDialog.showMessageDialog(textGUI, "ОШИБКА", "Нет ответа от устройства. \n\r Попробуйте сменить IP адрес");
            }
        }
    }
}
