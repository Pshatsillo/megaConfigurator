package ru.ablog.megad.configurator.windows;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ablog.megad.configurator.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    Label miscLbl;
    Label pwmLbl;
    Label smoothPWMLbl;
    TextBox ecmdVal;
    TextBox netTxtbox;
    TextBox miscVal;
    TextBox pwmVal;
    TextBox pwmDefVal;
    CheckBox netChkbox;
    ComboBox<MegaMModel> m;
    CheckBox mChkbox;
    CheckBox dChkbox;
    CheckBox smoothPWMChkbox;
    ComboBox<MegaDefDModel> defD;
    Button onButton;
    Button offButton;
    Button refreshButton;
    Button pwmButSet;
    Button saveButton;

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

        //log.info("message from mega is {}", pas);
        //log.info("mega type is <{}>", megaType.trim());
        //log.info("firmware type is <{}>", firmware);

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
            actionListBox.addItem("Port " + i + "- " + ports.get(i).getSelectedPTY().toString(), () -> loadport(finalI));
        }
        loadport(0);
        main.addComponent(actionListBox);
        MegaConfig.gm.refreshWindow(main.withBorder(Borders.doubleLine(megaType.trim() + " firmware: " + firmware + "   IP:" + deviceAddress)));
       /* Window window = new BasicWindow();
        window.setComponent(main.withBorder(Borders.doubleLine(megaType.trim() + " firmware: " + firmware + "   IP:" + deviceAddress)));
        window.setHints(Arrays.asList(Window.Hint.CENTERED));
        Collection<Window> windows = textGUI.getWindows();

        textGUI.addWindow(window);*/


    }

    private void loadport(int i) {
        //log.info("loadport {}", i);
        MegaDPortModel port = ports.get(i);

        if (megaType.trim().equals("MegaD-328")) {
            switch (port.getSelectedPTY().toString()) {
                case "In":
                    removeDefaultOutput();
                    removeOnOFFButtons();
                    removeVal();
                    removeSmoothPWM();

                    addPortstat(i, 15, 0);
                    addType(i, 15, 1);
                    addAct(i, 15, 2);
                    addNet(i, 15, 3);
                    addMode(i, 15, 4);
                    addRaw(i, 15, 5);
                    addRefreshButton(i, 15, 6);
                    addSaveButton(i, 15, 7);
                    break;
                case "Out":
                    removeAct();
                    removeNet();
                    removeMode();
                    removeRaw();
                    removeVal();
                    removeSmoothPWM();

                    addPortstat(i, 15, 0);
                    addOnOFFButtons(i, 15, 1);
                    addType(i, 15, 2);
                    addDefaultOutput(i, 15, 3);
                    addMode(i, 15, 4);
                    addSmoothPWM(i, 15, 5);
                    addRefreshButton(i, 15, 6);
                    addSaveButton(i, 15, 7);
                    break;
                case "ADC":
                    removeOnOFFButtons();
                    removeType();
                    removeAct();
                    removeNet();
                    removeMode();
                    removeRaw();
                    removeDefaultOutput();
                    removeSmoothPWM();

                    addPortstat(i, 15, 0);
                    addMode(i, 15, 1);
                    addVal(i, 15, 2);
                    addAct(i, 15, 3);
                    addNet(i, 15, 4);
                    addRefreshButton(i, 15, 5);
                    addSaveButton(i, 15, 7);
                    break;
            }
        }


    }


    private void addType(int portnumber, int col, int row) {
        if (ptyCombo != null) {
            main.removeComponent(ptyCombo);
            ptyCombo = null;
        }
        if (typeLbl != null) {
            main.removeComponent(typeLbl);
            typeLbl = null;
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
            ptyCombo = null;
        }
        if (typeLbl != null) {
            main.removeComponent(typeLbl);
            typeLbl = null;
        }
    }

    private void addPortstat(int portnumber, int col, int row) {

        if (portstat != null) {
            main.removeComponent(portstat);
            portstat = null;
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
            ecmd = null;
        }
        if (ecmdVal != null) {
            main.removeComponent(ecmdVal);
            ecmdVal = null;
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
            ecmd = null;
        }
        if (ecmdVal != null) {
            main.removeComponent(ecmdVal);
            ecmdVal = null;
        }
    }

    private void addNet(int portnumber, int col, int row) {
        MegaDPortModel port = ports.get(portnumber);

        if (netLbl != null) {
            main.removeComponent(netLbl);
            netLbl = null;
        }

        if (netTxtbox != null) {
            main.removeComponent(netTxtbox);
            netTxtbox = null;
        }

        if (netChkbox != null) {
            main.removeComponent(netChkbox);
            netChkbox = null;
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
            netLbl = null;
        }

        if (netTxtbox != null) {
            main.removeComponent(netTxtbox);
            netTxtbox = null;
        }

        if (netChkbox != null) {
            main.removeComponent(netChkbox);
            netChkbox = null;
        }
    }

    private void addMode(int portnumber, int col, int row) {
        MegaDPortModel port = ports.get(portnumber);

        if (m != null) {
            main.removeComponent(m);
            m = null;
        }

        if (modeLbl != null) {
            main.removeComponent(modeLbl);
            modeLbl = null;
        }

        if (mChkbox != null) {
            main.removeComponent(mChkbox);
            mChkbox = null;
        }

        if (port.getM() != null) {
            modeLbl = new Label("Mode:");
            modeLbl.setPosition(new TerminalPosition(col, row));
            modeLbl.setSize(new TerminalSize(modeLbl.getText().length(), 1));

            m = new ComboBox<>(port.getM());
            m.setSelectedItem(port.getSelectedM());
            m.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER));
            m.setPosition(new TerminalPosition(modeLbl.getPosition().getColumn() + modeLbl.getText().length(), row));
            m.setPreferredSize(new TerminalSize(7, 1));
            m.setSize(new TerminalSize(7, 1));

            if (!port.getSelectedPTY().toString().equals("ADC") && (port.getM().toString().contains("P&R"))) {
                mChkbox = new CheckBox();
                mChkbox.setSize(new TerminalSize(3, 1));
                mChkbox.setPosition(new TerminalPosition(m.getPosition().getColumn() + m.getSize().getColumns(), row));
                mChkbox.setChecked(port.getMisc());
                main.addComponent(mChkbox);
            }
            main.addComponent(modeLbl);
            main.addComponent(m);
        } else {
            if (m != null) {
                main.removeComponent(m);
                m = null;
            }

            if (modeLbl != null) {
                main.removeComponent(modeLbl);
                modeLbl = null;
            }

            if (mChkbox != null) {
                main.removeComponent(mChkbox);
                mChkbox = null;
            }
        }


    }

    private void removeMode() {
        if (m != null) {
            main.removeComponent(m);
            m = null;
        }

        if (modeLbl != null) {
            main.removeComponent(modeLbl);
            modeLbl = null;
        }

        if (mChkbox != null) {
            main.removeComponent(mChkbox);
            mChkbox = null;
        }

    }

    private void addRaw(int portnumber, int col, int row) {
        MegaDPortModel port = ports.get(portnumber);

        if (rawLbl != null) {
            main.removeComponent(rawLbl);
            rawLbl = null;
        }

        if (dChkbox != null) {
            main.removeComponent(dChkbox);
            dChkbox = null;
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
            rawLbl = null;
        }

        if (dChkbox != null) {
            main.removeComponent(dChkbox);
            dChkbox = null;
        }

    }

    private void addDefaultOutput(int portnumber, int col, int row) {
        MegaDPortModel port = ports.get(portnumber);

        if (defDLbl != null) {
            main.removeComponent(defDLbl);
            defDLbl = null;
        }

        if (defD != null) {
            main.removeComponent(defD);
            defD = null;
        }

        if (pwmDefVal != null) {
            main.removeComponent(pwmDefVal);
            pwmDefVal = null;
        }


        try {
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
        } catch (Exception ex) {
            defDLbl = new Label("Default:");
            defDLbl.setPosition(new TerminalPosition(col, row));
            defDLbl.setSize(new TerminalSize(defDLbl.getText().length(), 1));
            main.addComponent(defDLbl);

            pwmDefVal = new TextBox(port.getDefPWM());
            pwmDefVal.setSize(new TerminalSize(5, 1));
            pwmDefVal.setPosition(new TerminalPosition(defDLbl.getPosition().getColumn() + defDLbl.getText().length(), row));
            main.addComponent(pwmDefVal);

        }
    }

    private void removeDefaultOutput() {
        if (defDLbl != null) {
            main.removeComponent(defDLbl);
            defDLbl = null;
        }

        if (defD != null) {
            main.removeComponent(defD);
            defD = null;
        }

        if (pwmDefVal != null) {
            main.removeComponent(pwmDefVal);
            pwmDefVal = null;
        }

    }

    private void addOnOFFButtons(int i, int col, int row) {
        MegaDPortModel port = ports.get(i);
        if (onButton != null) {
            main.removeComponent(onButton);
            onButton = null;
        }

        if (offButton != null) {
            main.removeComponent(offButton);
            offButton = null;
        }


        if (pwmLbl != null) {
            main.removeComponent(pwmLbl);
            pwmLbl = null;
        }

        if (pwmVal != null) {
            main.removeComponent(pwmVal);
            pwmVal = null;
        }

        if (pwmButSet != null) {
            main.removeComponent(pwmButSet);
            pwmButSet = null;
        }


        if (port.getSelectedM() != null) {
            if (port.getSelectedM().toString().equals("SW")) {
                onButton = new Button("ON", () -> {
                    try {
                        http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + i + "&cmd=" + i + ":1");
                        port.refresh(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + i));
                        loadport(i);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                onButton.setPosition(new TerminalPosition(col, row));
                onButton.setSize(new TerminalSize(onButton.getLabel().length() + 2, 1));
                onButton.setPreferredSize(new TerminalSize(onButton.getLabel().length(), 1));


                offButton = new Button("OFF", () -> {
                    try {
                        http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + i + "&cmd=" + i + ":0");
                        port.refresh(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + i));
                        loadport(i);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                offButton.setPosition(new TerminalPosition(onButton.getPosition().getColumn() + onButton.getSize().getColumns(), row));
                offButton.setSize(new TerminalSize(offButton.getLabel().length() + 2, 1));

                main.addComponent(onButton);
                main.addComponent(offButton);
            } else if (port.getSelectedM().toString().equals("PWM")) {
                pwmLbl = new Label("PWM (0-255):");
                pwmLbl.setPosition(new TerminalPosition(col, row));
                pwmLbl.setSize(new TerminalSize(pwmLbl.getText().length(), 1));
                main.addComponent(pwmLbl);

                pwmVal = new TextBox(port.getPwm());
                pwmVal.setSize(new TerminalSize(5, 1));
                pwmVal.setPosition(new TerminalPosition(pwmLbl.getPosition().getColumn() + pwmLbl.getText().length(), row));
                main.addComponent(pwmVal);

                pwmButSet = new Button("Set", () -> {
                    try {
                        http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + i + "&cmd=" + i + ":" + pwmVal.getText());
                        port.refresh(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + i));
                        loadport(i);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                pwmButSet.setPosition(new TerminalPosition(pwmVal.getPosition().getColumn() + pwmVal.getSize().getColumns(), row));
                pwmButSet.setSize(new TerminalSize(pwmButSet.getLabel().length() + 2, 1));
                main.addComponent(pwmButSet);

            }
        } else {
            onButton = new Button("ON", () -> {
                try {
                    http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + i + "&cmd=" + i + ":1");
                    port.refresh(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + i));
                    loadport(i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            onButton.setPosition(new TerminalPosition(col, row));
            onButton.setSize(new TerminalSize(onButton.getLabel().length() + 2, 1));
            onButton.setPreferredSize(new TerminalSize(onButton.getLabel().length(), 1));


            offButton = new Button("OFF", () -> {
                try {
                    http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + i + "&cmd=" + i + ":0");
                    port.refresh(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + i));
                    loadport(i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            offButton.setPosition(new TerminalPosition(onButton.getPosition().getColumn() + onButton.getSize().getColumns(), row));
            offButton.setSize(new TerminalSize(offButton.getLabel().length() + 2, 1));

            main.addComponent(onButton);
            main.addComponent(offButton);
        }
    }

    private void removeOnOFFButtons() {
        if (onButton != null) {
            main.removeComponent(onButton);
            onButton = null;
        }

        if (offButton != null) {
            main.removeComponent(offButton);
            offButton = null;
        }

        if (pwmLbl != null) {
            main.removeComponent(pwmLbl);
            pwmLbl = null;
        }

        if (pwmVal != null) {
            main.removeComponent(pwmVal);
            pwmVal = null;
        }

    }


    private void addVal(int portnumber, int col, int row) {
        MegaDPortModel port = ports.get(portnumber);

        if (miscLbl != null) {
            main.removeComponent(miscLbl);
            miscLbl = null;
        }

        if (miscVal != null) {
            main.removeComponent(miscVal);
            miscVal = null;
        }

        miscLbl = new Label("Val:");
        miscLbl.setPosition(new TerminalPosition(col, row));
        miscLbl.setSize(new TerminalSize(miscLbl.getText().length(), 1));

        miscVal = new TextBox(port.getMiscVal());
        miscVal.setSize(new TerminalSize(15, 1));
        miscVal.setPosition(new TerminalPosition(miscLbl.getPosition().getColumn() + miscLbl.getText().length(), row));

        main.addComponent(miscLbl);
        main.addComponent(miscVal);

    }

    private void removeVal() {
        if (netLbl != null) {
            main.removeComponent(netLbl);
            netLbl = null;
        }

        if (netTxtbox != null) {
            main.removeComponent(netTxtbox);
            netTxtbox = null;
        }

        if (netChkbox != null) {
            main.removeComponent(netChkbox);
            netChkbox = null;
        }
    }

    private void addSmoothPWM(int portnumber, int col, int row) {
        MegaDPortModel port = ports.get(portnumber);

        if (smoothPWMLbl != null) {
            main.removeComponent(smoothPWMLbl);
            smoothPWMLbl = null;
        }

        if (smoothPWMChkbox != null) {
            main.removeComponent(smoothPWMChkbox);
            smoothPWMChkbox = null;
        }

        smoothPWMLbl = new Label("Smooth:");
        smoothPWMLbl.setPosition(new TerminalPosition(col, row));
        smoothPWMLbl.setSize(new TerminalSize(smoothPWMLbl.getText().length(), 1));

        smoothPWMChkbox = new CheckBox();
        smoothPWMChkbox.setSize(new TerminalSize(3, 1));
        smoothPWMChkbox.setPosition(new TerminalPosition(smoothPWMLbl.getPosition().getColumn() + smoothPWMLbl.getText().length(), row));
        smoothPWMChkbox.setChecked(port.getD());

        main.addComponent(smoothPWMLbl);
        main.addComponent(smoothPWMChkbox);
    }

    private void removeSmoothPWM() {
        if (smoothPWMLbl != null) {
            main.removeComponent(smoothPWMLbl);
            smoothPWMLbl = null;
        }

        if (smoothPWMChkbox != null) {
            main.removeComponent(smoothPWMChkbox);
            smoothPWMChkbox = null;
        }
    }

    private void addRefreshButton(int portnumber, int col, int row) {
        if (refreshButton != null) {
            main.removeComponent(refreshButton);
        }


        refreshButton = new Button("Refresh", () -> {
            MegaDPortModel port = ports.get(portnumber);
            try {
                port.refresh(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + portnumber));
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadport(portnumber);
        });
        refreshButton.setPosition(new TerminalPosition(col, row));
        refreshButton.setSize(new TerminalSize(refreshButton.getLabel().length() + 2, 1));
        refreshButton.setPreferredSize(new TerminalSize(refreshButton.getLabel().length(), 1));

        main.addComponent(refreshButton);
    }

    private void addSaveButton(int i, int col, int row) {
        MegaDPortModel port = ports.get(i);
        if (saveButton != null) {
            main.removeComponent(saveButton);
        }
        saveButton = new Button("Save", () -> {

            String saveValues = "";
            saveValues += "pn=" + i;
            if (ptyCombo != null) {
                saveValues += "&pty=" + ptyCombo.getSelectedItem().getSelectedValue();
            }
            if (ecmdVal != null) {
                saveValues += "&ecmd=" + convertToHtml(ecmdVal.getText());
            }
            if (netTxtbox != null) {
                saveValues += "&eth=" + convertToHtml(netTxtbox.getText());
            }
            if (netChkbox != null) {
                if (netChkbox.isChecked()) {
                    saveValues += "&naf=1";
                }
            }
            if (m != null) {
                saveValues += "&m=" + m.getSelectedItem().getSelectedValue();
            }
            if (mChkbox != null) {
                if (mChkbox.isChecked()) {
                    saveValues += "&misc=1";
                }
            }
            if (dChkbox != null) {
                if (dChkbox.isChecked()) {
                    saveValues += "&d=1";
                }
            }
            if (defD != null) {
                saveValues += "&d=" + defD.getSelectedItem().getSelectedValue();
            }
            if (pwmDefVal != null) {
                saveValues += "&d=" + pwmDefVal.getText();
            }
            if (smoothPWMChkbox != null) {
                if (smoothPWMChkbox.isChecked()) {
                    saveValues += "&misc=1";
                }
            }
            if (miscVal != null) {
                saveValues += "&misc=" + miscVal.getText();
            }


            //http://192.168.10.18/sec/?pn=11&pty=0&ecmd=13%3A255&eth=&m=1
            //http://192.168.10.18/sec/?pn=11&pty=0&ecmd=&eth=localhost&naf=1&m=0&misc=1&d=1
            //http://192.168.10.18/sec/?pn=10&pty=1&d=0&m=1
            try {
                http.connectToMega("http://" + deviceAddress + "/" + pass + "/?" + saveValues);
                port.refresh(http.connectToMega("http://" + deviceAddress + "/" + pass + "/?pt=" + i));
                loadport(i);
            } catch (IOException e) {
                e.printStackTrace();
            }
            new MessageDialogBuilder()
                    .setTitle("")
                    .setText("Saved")
                    .addButton(MessageDialogButton.OK)
                    .build()
                    .showDialog(genGUI.textGUI);
        });
        saveButton.setPosition(new TerminalPosition(col, row));
        saveButton.setSize(new TerminalSize(saveButton.getLabel().length() + 2, 1));
        saveButton.setPreferredSize(new TerminalSize(saveButton.getLabel().length(), 1));

        main.addComponent(saveButton);
    }


    private void loadportpref() {
        log.info("loadportpref");
    }

    private String convertToHtml(String val) {
        String res = "";
        try {
            res = URLEncoder.encode(val, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //res = val.replace(":", "%3A");

        return res;
    }

}
