package ru.ablog.megad.configurator.windows;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ablog.megad.configurator.*;

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

    String megaType;
    ComboBox<MegaPTYmodel> ptyCombo;
    Label portstat;
    Label ecmd;
    Label netLbl;
    Label typeLbl;
    Label modeLbl;
    Label rawLbl;
    Label defDLbl;
    TextBox ecmdVal;
    TextBox netTxtbox;
    CheckBox netChkbox;
    ComboBox<MegaMModel> m;
    CheckBox mChkbox;
    CheckBox dChkbox;
    ComboBox<MegaDefDModel> defD;
    Button onButton;
    Button offButton;

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
        enter.addListener((button -> waitscreen(password)));
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
                    new Thread(() -> {
                        try {
                            generateWindow(pas);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void generateWindow(String pas) throws IOException {

        megaType = pas.split("by")[0];
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
            }
            //    config = Jsoup.parse(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?cf=1"));
            //    mega_id = Jsoup.parse(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?cf=2"));

        } else if (megaType.trim().equals("MegaD-2561")) {

            for (int i = 0; i <= 37; i++) {
                MegaDPortModel port = new MegaDPortModel(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + i));
                ports.put(i, port);
            }
            //   config = Jsoup.parse(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?cf=1"));
            //   mega_id = Jsoup.parse(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?cf=2"));
        }


        main.setLayoutManager(new AbsoluteLayout());
        main.withBorder(Borders.singleLine());
        MegaActionListbox actionListBox = new MegaActionListbox();
        actionListBox.setSize(new TerminalSize(15, 15));
        actionListBox.setPosition(new TerminalPosition(0, 0));
        actionListBox.withBorder(Borders.singleLine());
        actionListBox.takeFocus();
        for (int i = 0; i < ports.size(); i++) {
            int finalI = i;
            actionListBox.addItem("Port " + i + "- " + ports.get(i).getSelectedPTY().toString(), () -> loadport(finalI)); //сделать отдельный объект
        }
        loadport(0);
        main.addComponent(actionListBox);
        MegaConfig.gm.refreshWindow(main.withBorder(Borders.doubleLine(megaType.trim() + " firmware: " + firmware + "   IP:" + deviceAddress)));

    }

    private void loadport(int i) {
        log.info("loadport {}", i);
        MegaDPortModel port = ports.get(i);

        if (megaType.trim().equals("MegaD-328")) {
            switch (port.getSelectedPTY().toString()) {
                case "In":
                    removeDefaultOutput();

                    addPortstat(i, 15, 0);
                    addType(i, 15, 1);
                    addAct(i, 15, 2);
                    addNet(i, 15, 3);
                    addMode(i, 15, 4);
                    addRaw(i, 15, 5);

                    break;
                case "Out":
                    removeAct();
                    removeNet();
                    removeMode();
                    removeRaw();

                    addPortstat(i, 15, 0);

                    addType(i, 15, 2);
                    addDefaultOutput(i, 15, 3);

                    break;
                case "ADC":
                    removeType();
                    removeAct();
                    removeNet();
                    removeMode();
                    removeRaw();
                    removeDefaultOutput();

                    addPortstat(i, 15, 0);
                    break;
            }
        }


    }


    private void addType(int portnumber, int col, int row) {
        if (ptyCombo != null) {
            main.removeComponent(ptyCombo);
        }
        if (typeLbl != null) {
            main.removeComponent(typeLbl);
        }

        typeLbl = new Label("Type:");
        typeLbl.setPosition(new TerminalPosition(col, row));
        typeLbl.setSize(new TerminalSize("Type:".length(), 1));

        MegaDPortModel port = ports.get(portnumber);
        ptyCombo = new ComboBox<>(port.getPTY());
        ptyCombo.setSelectedItem(port.getSelectedPTY());
        ptyCombo.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER));
        ptyCombo.setPosition(new TerminalPosition(15 + typeLbl.getText().length(), row));
        ptyCombo.setPreferredSize(new TerminalSize(7, 1));
        ptyCombo.setSize(new TerminalSize(7, 1));
        main.addComponent(ptyCombo);
        main.addComponent(typeLbl);
    }

    private void removeType() {
        if (ptyCombo != null) {
            main.removeComponent(ptyCombo);
        }
        if (typeLbl != null) {
            main.removeComponent(typeLbl);
        }
    }

    private void addPortstat(int portnumber, int col, int row) {

        if (portstat != null) {
            main.removeComponent(portstat);
        }
        MegaDPortModel port = ports.get(portnumber);
        portstat = new Label(port.portStatus);
        portstat.setPosition(new TerminalPosition(col, row));
        portstat.setSize(new TerminalSize(port.portStatus.length(), 1));
        main.addComponent(portstat);
    }

    private void addAct(int portnumber, int col, int row) {
        MegaDPortModel port = ports.get(portnumber);

        if (ecmd != null) {
            main.removeComponent(ecmd);
        }
        if (ecmdVal != null) {
            main.removeComponent(ecmdVal);
        }
        ecmd = new Label("Act:");
        ecmd.setPosition(new TerminalPosition(col, row));
        ecmd.setSize(new TerminalSize(ecmd.getText().length(), 1));

        ecmdVal = new TextBox(port.getECMD());
        ecmdVal.setSize(new TerminalSize(15, 1));
        ecmdVal.setPosition(new TerminalPosition(ecmd.getPosition().getColumn() + ecmd.getText().length(), row));

        main.addComponent(ecmd);
        main.addComponent(ecmdVal);
    }

    private void removeAct() {
        if (ecmd != null) {
            main.removeComponent(ecmd);
        }
        if (ecmdVal != null) {
            main.removeComponent(ecmdVal);
        }
    }

    private void addNet(int portnumber, int col, int row) {
        MegaDPortModel port = ports.get(portnumber);

        if (netLbl != null) {
            main.removeComponent(netLbl);
        }

        if (netTxtbox != null) {
            main.removeComponent(netTxtbox);
        }

        if (netChkbox != null) {
            main.removeComponent(netChkbox);
        }

        netLbl = new Label("Net:");
        netLbl.setPosition(new TerminalPosition(col, row));
        netLbl.setSize(new TerminalSize(netLbl.getText().length(), 1));

        netTxtbox = new TextBox(port.getEth());
        netTxtbox.setSize(new TerminalSize(15, 1));
        netTxtbox.setPosition(new TerminalPosition(netLbl.getPosition().getColumn() + netLbl.getText().length(), row));

        netChkbox = new CheckBox();
        netChkbox.setSize(new TerminalSize(3, 1));
        netChkbox.setPosition(new TerminalPosition(netTxtbox.getPosition().getColumn() + netTxtbox.getSize().getColumns(), row));
        netChkbox.setChecked(port.getNaf());

        main.addComponent(netChkbox);
        main.addComponent(netTxtbox);
        main.addComponent(netLbl);

    }

    private void removeNet() {
        if (netLbl != null) {
            main.removeComponent(netLbl);
        }

        if (netTxtbox != null) {
            main.removeComponent(netTxtbox);
        }

        if (netChkbox != null) {
            main.removeComponent(netChkbox);
        }
    }

    private void addMode(int portnumber, int col, int row) {
        MegaDPortModel port = ports.get(portnumber);

        if (m != null) {
            main.removeComponent(m);
        }

        if (modeLbl != null) {
            main.removeComponent(modeLbl);
        }

        if (mChkbox != null) {
            main.removeComponent(mChkbox);
        }

        modeLbl = new Label("Mode:");
        modeLbl.setPosition(new TerminalPosition(col, row));
        modeLbl.setSize(new TerminalSize(modeLbl.getText().length(), 1));

        m = new ComboBox<>(port.getM());
        m.setSelectedItem(port.getSelectedM());
        m.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER));
        m.setPosition(new TerminalPosition(modeLbl.getPosition().getColumn() + modeLbl.getText().length(), row));
        m.setPreferredSize(new TerminalSize(7, 1));
        m.setSize(new TerminalSize(7, 1));

        mChkbox = new CheckBox();
        mChkbox.setSize(new TerminalSize(3, 1));
        mChkbox.setPosition(new TerminalPosition(m.getPosition().getColumn() + m.getSize().getColumns(), row));
        mChkbox.setChecked(port.getMisc());

        main.addComponent(modeLbl);
        main.addComponent(m);
        main.addComponent(mChkbox);
    }

    private void removeMode() {
        if (m != null) {
            main.removeComponent(m);
        }

        if (modeLbl != null) {
            main.removeComponent(modeLbl);
        }

        if (mChkbox != null) {
            main.removeComponent(mChkbox);
        }

    }

    private void addRaw(int portnumber, int col, int row) {
        MegaDPortModel port = ports.get(portnumber);

        if (rawLbl != null) {
            main.removeComponent(rawLbl);
        }

        if (dChkbox != null) {
            main.removeComponent(dChkbox);
        }

        rawLbl = new Label("Raw:");
        rawLbl.setPosition(new TerminalPosition(col, row));
        rawLbl.setSize(new TerminalSize(rawLbl.getText().length(), 1));

        dChkbox = new CheckBox();
        dChkbox.setSize(new TerminalSize(3, 1));
        dChkbox.setPosition(new TerminalPosition(rawLbl.getPosition().getColumn() + rawLbl.getText().length(), row));
        dChkbox.setChecked(port.getD());

        main.addComponent(rawLbl);
        main.addComponent(dChkbox);
    }

    private void removeRaw() {
        if (rawLbl != null) {
            main.removeComponent(rawLbl);
        }

        if (dChkbox != null) {
            main.removeComponent(dChkbox);
        }

    }

    private void addDefaultOutput(int portnumber, int col, int row) {
        MegaDPortModel port = ports.get(portnumber);

        if (defDLbl != null) {
            main.removeComponent(defDLbl);
        }

        if (defD != null) {
            main.removeComponent(defD);
        }

        defDLbl = new Label("Default:");
        defDLbl.setPosition(new TerminalPosition(col, row));
        defDLbl.setSize(new TerminalSize(defDLbl.getText().length(), 1));

        defD = new ComboBox<>(port.getDefD());
        defD.setSelectedItem(port.getSelectedDefD());
        defD.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER));
        defD.setPosition(new TerminalPosition(defDLbl.getPosition().getColumn() + defDLbl.getText().length(), row));
        defD.setPreferredSize(new TerminalSize(3, 1));
        defD.setSize(new TerminalSize(3, 1));

        main.addComponent(defDLbl);
        main.addComponent(defD);
    }

    private void removeDefaultOutput() {
        if (defDLbl != null) {
            main.removeComponent(defDLbl);
        }

        if (defD != null) {
            main.removeComponent(defD);
        }

    }

    private void addOnOFFButtons(int i) {
        onButton = new Button("ON", () -> {
            try {
                http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadportpref() {
        log.info("loadportpref");
    }

}
