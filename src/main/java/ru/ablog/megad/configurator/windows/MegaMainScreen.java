package ru.ablog.megad.configurator.windows;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ablog.megad.configurator.MegaActionListbox;
import ru.ablog.megad.configurator.MegaConfig;
import ru.ablog.megad.configurator.MegaDPortModel;
import ru.ablog.megad.configurator.MegaHTTPConnect;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MegaMainScreen {
    String deviceAddress;
    String pass;
    WindowBasedTextGUI textGUI;
    Logger log = LoggerFactory.getLogger(MegaMainScreen.class);
    Panel mainPanel;
    MegaHTTPConnect http = new MegaHTTPConnect();
    Window window;
    Panel main = new Panel();
    Map<Integer, MegaDPortModel> ports = new HashMap<>();
    Panel props = new Panel();

    public MegaMainScreen(String inetAddress, WindowBasedTextGUI textGUI) {
        deviceAddress = inetAddress;
        this.textGUI = textGUI;
    }

    void getHTTPsettings(String deviceAddress) throws IOException {

        http.connectToMega("http://" + deviceAddress + "/");
        //String r[] = responseString.split("<br>");

        //getting firmware version
        //firmwareVersion = r[0].substring(r[0].indexOf('(')+5, r[0].indexOf(')'));
    }


    public Component show() {
        mainPanel = new Panel();
        mainPanel.setLayoutManager(new AbsoluteLayout());

        Label label = new Label("Enter password: ");
        label.setPosition(new TerminalPosition(1, 0));
        label.setSize(new TerminalSize(17, 1));

        TextBox password = new TextBox();
        password.setText("sec");
        password.setPosition(new TerminalPosition(17, 0));
        password.setSize(new TerminalSize(8, 1));

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
        enter.addListener((button -> {
            waitscreen(password);
        }));
        enter.setPosition(new TerminalPosition(17, 1));
        enter.setSize(new TerminalSize(7, 1));
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
        if (password.getText().length() > 5) {
            MessageDialog.showMessageDialog(textGUI, "Error", "Password must be 5 symbols maximum");
        } else {
            MegaHTTPConnect http = new MegaHTTPConnect();
            try {
                String pas = http.connectToMega("http://" + deviceAddress + "/" + password.getText());
                //log.info("pass response {}", pas);
                if (pas.equals("Unauthorized")) {
                    MessageDialog.showMessageDialog(textGUI, "Error", "Wrong password");
                } else {
                    pass = password.getText();
                    Panel wait = new Panel();
                    wait.setLayoutManager(new AbsoluteLayout());
                    Label label = new Label("Please wait \n\rLoading config from mega...");
                    label.setPosition(new TerminalPosition(1, 0));
                    label.setSize(new TerminalSize(30, 2));
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
        String firmware = pas.split("<br>")[0].substring(pas.split("<br>")[0].indexOf('(') + 5, pas.split("<br>")[0].indexOf(')'));

        Document config;
        Document mega_id;

        log.info("message from mega is {}", pas);
        log.info("mega type is <{}>", megaType.trim());
        log.info("firmware type is <{}>", firmware);

        if (megaType.trim().equals("MegaD-328")) {
            for (int i = 0; i <= 15; i++) {
                MegaDPortModel port = new MegaDPortModel(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + i));

                ports.put(i, port);
                //log.info("port" + i + " value <{}>", port.outerHtml());
            }
            config = Jsoup.parse(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?cf=1"));
            mega_id = Jsoup.parse(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?cf=2"));

        } else if (megaType.trim().equals("MegaD-2561")) {

            for (int i = 0; i <= 37; i++) {
                MegaDPortModel port = new MegaDPortModel(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + i));

                // Document port = Jsoup.parse(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + i));
                ports.put(i, port);
                //log.info("port" + i + " value <{}>", port.outerHtml());
            }
            config = Jsoup.parse(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?cf=1"));
            mega_id = Jsoup.parse(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?cf=2"));
        }


        main.setLayoutManager(new AbsoluteLayout());
        main.withBorder(Borders.singleLine());
        MegaActionListbox actionListBox = new MegaActionListbox();
        actionListBox.setSize(new TerminalSize(10, 15));
        actionListBox.setPosition(new TerminalPosition(0, 0));
        actionListBox.withBorder(Borders.singleLine());
        actionListBox.takeFocus();
        for (int i = 0; i < ports.size(); i++) {
            int finalI = i;
            actionListBox.addItem("Port" + i, () -> loadport(finalI)); //сделать отдельный объект
        }
        loadport(0);
        //actionListBox.addListener(this::loadportpref);


        main.addComponent(actionListBox);
        //main.addComponent(modeCB);
        //props.addComponent();
        MegaConfig.gm.refreshWindow(main.withBorder(Borders.doubleLine(megaType.trim() + " firmware: " + firmware)));

    }

    private void loadport(int i) {
        //log.info("loadport {}",i);
        //props.addComponent(getPTYComboBox(i));
    }

    ComboBox getPTYComboBox(int i) {
        ComboBox<String> modeCB;
        /*Elements dd = ports.get(i).getElementsByAttributeValue("name", "pty").select("select > option");
        if (dd.size() > 0) {
            Elements selected = dd.select("option[selected]");
            HashMap<String, Integer> pn = new HashMap<String, Integer>();
            ArrayList<String> cbpn = new ArrayList<>();
            for (Element mode : dd) {
                pn.put(mode.text(), Integer.parseInt(mode.attr("value")));
                cbpn.add(mode.text());
            }

            modeCB = new ComboBox<String>(cbpn);
            modeCB.setSelectedItem(selected.text());
            modeCB.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER));
            modeCB.setPosition(new TerminalPosition(15, 0));
            modeCB.setPreferredSize(new TerminalSize(7, 1));
            modeCB.setSize(new TerminalSize(7, 1));
            //modeCB.setSelectedIndex(selectedindex);
            return modeCB;
        } else {
            modeCB = new ComboBox<String>();
            modeCB.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER));
            modeCB.setPosition(new TerminalPosition(15, 0));
            modeCB.setPreferredSize(new TerminalSize(7, 1));
            modeCB.setSize(new TerminalSize(7, 1));
            //modeCB.withBorder(Borders.singleLine());
            modeCB.clearItems();
            modeCB.setEnabled(false);
            return modeCB;
        }*/
        return null;
    }

    private void loadportpref() {
        log.info("loadportpref");
    }
}
