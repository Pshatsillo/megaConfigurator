package ru.ablog.megad.configurator.windows;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.graphics.ThemeStyle;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import javafx.scene.layout.Pane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ablog.megad.configurator.MegaConfig;
import ru.ablog.megad.configurator.MegaHTTPConnect;

import java.io.IOException;
import java.util.*;

public class MegaMainScreen {
    String deviceAddress;
    String pass;
    WindowBasedTextGUI textGUI;
    Logger log = LoggerFactory.getLogger(MegaMainScreen.class);
    Panel mainPanel;
    MegaHTTPConnect http = new MegaHTTPConnect();
    Window window;
    public MegaMainScreen(String inetAddress, Object textGUI) {
        deviceAddress = inetAddress;
        this.textGUI = (WindowBasedTextGUI) textGUI;
    }

    void getHTTPsettings(String deviceAddress) throws IOException {

        http.connectToMega("http://"+deviceAddress+"/");
        //String r[] = responseString.split("<br>");

        //getting firmware version
        //firmwareVersion = r[0].substring(r[0].indexOf('(')+5, r[0].indexOf(')'));
    }


    public Component show() {
        mainPanel = new Panel();
        mainPanel.setLayoutManager(new AbsoluteLayout());

        Label label = new Label("Enter password: ");
        label.setPosition(new TerminalPosition(1,0));
        label.setSize(new TerminalSize(17,1));

        TextBox password = new TextBox();
        password.setText("sec");
        password.setPosition(new TerminalPosition(17,0));
        password.setSize(new TerminalSize(8,1));

        Button enter = new Button("Enter");
        /*Button enter = new Button("Enter", new Runnable() {
            @Override
            public void run() {
                if(password.getText().length() > 5){
                    MessageDialog.showMessageDialog(textGUI,"Error", "Password must be 5 symbols maximum");
                } else {
                    MegaHTTPConnect http = new MegaHTTPConnect();
                    try {
                        String pas = http.connectToMega("http://" + deviceAddress + "/" + password.getText());
                        //log.info("pass response {}", pas);
                        if(pas.equals("Unauthorized")){
                            MessageDialog.showMessageDialog(textGUI,"Error", "Wrong password");
                        } else {
                            pass = password.getText();
                            Panel wait = new Panel();
                            wait.setLayoutManager(new AbsoluteLayout());
                            Label label = new Label("Please wait \n\rLoading config from mega...");
                            label.setPosition(new TerminalPosition(1,0));
                            label.setSize(new TerminalSize(30,2));
                            wait.addComponent(label);
                            MegaConfig.gm.refreshWindow(wait);
                            generateWindow(pas);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/
        enter.addListener((button -> {waitscreen(password);}));
        enter.setPosition(new TerminalPosition(17, 1));
        enter.setSize(new TerminalSize(7,1));
/*        ProgressBar pb = new ProgressBar();
        pb.setPosition(new TerminalPosition(2,0));
        pb.setSize(new TerminalSize(9,2));
        mainPanel.addComponent(pb);*/
        mainPanel.addComponent(label);
        mainPanel.addComponent(password);
        mainPanel.addComponent(enter);
        return mainPanel;
    }

    private void waitscreen(TextBox password) {
        if(password.getText().length() > 5){
            MessageDialog.showMessageDialog(textGUI,"Error", "Password must be 5 symbols maximum");
        } else {
            MegaHTTPConnect http = new MegaHTTPConnect();
            try {
                String pas = http.connectToMega("http://" + deviceAddress + "/" + password.getText());
                //log.info("pass response {}", pas);
                if(pas.equals("Unauthorized")){
                    MessageDialog.showMessageDialog(textGUI,"Error", "Wrong password");
                } else {
                    pass = password.getText();
                    Panel wait = new Panel();
                    wait.setLayoutManager(new AbsoluteLayout());
                    Label label = new Label("Please wait \n\rLoading config from mega...");
                    label.setPosition(new TerminalPosition(1,0));
                    label.setSize(new TerminalSize(30,2));
                    wait.addComponent(label);
                    MegaConfig.gm.refreshWindow(wait);
                    //textGUI.updateScreen();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                generateWindow(pas);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void generateWindow(String pas) throws IOException {

        String megaType = pas.split("by")[0];
        String firmware = pas.split("<br>")[0].substring(pas.split("<br>")[0].indexOf('(')+5, pas.split("<br>")[0].indexOf(')'));
        Map<Integer, Document> ports = new HashMap<>();
        Document config;
        Document mega_id;

        log.info("message from mega is {}" , pas);
        log.info("mega type is <{}>" , megaType.trim());
        log.info("firmware type is <{}>" , firmware);

        if(megaType.trim().equals("MegaD-328")){
            for(int i=0; i <= 15; i++){
                Document port = Jsoup.parse(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + i));
                ports.put(i, port);
                log.info("port" + i + " value <{}>", port.outerHtml());
            }
            config = Jsoup.parse(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?cf=1"));
            mega_id = Jsoup.parse(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?cf=2"));

            //mainPanel.removeAllComponents();
        } else if(megaType.trim().equals("MegaD-2561")){

            for(int i=0; i <= 37; i++){
                Document port = Jsoup.parse(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + i));
                ports.put(i, port);
                log.info("port" + i + " value <{}>", port.outerHtml());
            }

            config = Jsoup.parse(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?cf=1"));
            mega_id = Jsoup.parse(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?cf=2"));

            //mainPanel.removeAllComponents();
        }
        Elements dd = ports.get(0).getElementsByAttributeValue("name", "pty").select("select > option");
        HashMap<String, Integer> pn = new HashMap<String, Integer>();
        ArrayList<String> cbpn = new ArrayList<>();
        for(Element mode: dd){
            pn.put(mode.text(), Integer.parseInt(mode.attr("value")));
            cbpn.add(mode.text());
        }
        Panel main = new Panel();
        main.setLayoutManager(new AbsoluteLayout());

        ActionListBox actionListBox = new ActionListBox();

        actionListBox.setSize(new TerminalSize(7, 3));
        actionListBox.setPosition(new TerminalPosition(1, 3));
        actionListBox.addItem("Port-1", new Runnable() {
            @Override
            public void run() {
                log.info("port - {}", actionListBox.getSelectedIndex());
            }
        });
        actionListBox.addItem("Port-2", new Runnable() {
            @Override
            public void run() {
                log.info("port - {}", actionListBox.getSelectedIndex());
            }
        });
        actionListBox.addItem("Port-3", new Runnable() {
            @Override
            public void run() {
                log.info("port - {}", actionListBox.getSelectedIndex());
            }
        });

        ComboBox<String> modeCB = new ComboBox<String>(cbpn);
        modeCB.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER));
        modeCB.setPosition(new TerminalPosition(0, 0));
        modeCB.setPreferredSize(new TerminalSize(7, 1));
        modeCB.setSize(new TerminalSize(7, 1));
        main.addComponent(modeCB);
        main.addComponent(actionListBox);

        MegaConfig.gm.refreshWindow(main.withBorder(Borders.doubleLine(megaType.trim() + " firmware: "+ firmware)));

    }
}
