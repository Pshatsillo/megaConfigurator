package ru.ablog.megad.configurator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MegaDPortModel {
    Logger log = LoggerFactory.getLogger(MegaDPortModel.class);
    String url;
    Document port;
    public String portStatus;
    String digitalvalue;

    List<MegaPTYmodel> pty = new ArrayList<>();
    MegaPTYmodel selectedPTY;
    String ecmd;
    boolean af;
    String eth;
    boolean naf;
    List<MegaMModel> m = new ArrayList<>();
    String selectedM;
    boolean misc;
    String miscVal;
    String grselected;
    HashMap<String, Integer> gr = new HashMap<>();
    String hst;
    boolean d;
    List<MegaDefDModel> defD = new ArrayList<>();
    String selectedDefD;
    boolean mt;
    String grp;
    String disp;
    String pwm;

    public MegaDPortModel(String connectToMega) {
        url = connectToMega;
        parse();
       // getPTY();
    }

    void setPTY() {
        Elements dd = port.getElementsByAttributeValue("name", "pty").select("select > option");
        if (dd.size() > 0) {
            for (Element mode : dd) {
                MegaPTYmodel ptyCombo = new MegaPTYmodel();
                ptyCombo.add(mode.text(), Integer.parseInt(mode.attr("value")));
                ptyCombo.setSelected(mode.select("option[selected]").hasAttr("selected"));
                pty.add(ptyCombo);
            }
        } else {
            pty = null;
        }
    }

    public MegaPTYmodel getSelectedPTY() {
        if (pty != null) {
            for (MegaPTYmodel sel : pty) {
                if (sel.selected) {
                    return sel;
                }
            }
        }
        return new MegaPTYmodel("ADC");
    }

    void setSelectedPTY(Elements selected) {
        //ptyCombo.setSelected(selected.text());
    }

    public List<MegaPTYmodel> getPTY() {
        return pty;
    }

    void setECMD() {
        if (!port.select("input[name=ecmd]").isEmpty()) {
            ecmd = port.select("input[name=ecmd]").attr("value");
        }
    }

    public String getECMD() {
        return ecmd;
    }

    void setAF() {
        if (!port.select("input[name=af]").select("input[type=checkbox]").isEmpty()) {
            misc = port.select("input[name=af]").select("input[checked]").hasAttr("checked");
        }
    }

    public boolean getAF(){
        return af;
    }

    void setEth() {
        if(!port.select("input[name=eth]").isEmpty()) {
            eth = port.select("input[name=eth]").attr("value");
        }
    }

    public String getEth() {
        return eth;
    }

    void setNaf() {
        naf = port.select("input[name=naf]").select("input[checked]").hasAttr("checked");
    }

    public boolean getNaf() {
        return naf;
    }

    void setM() {
        Elements dd = port.getElementsByAttributeValue("name", "m").select("select > option");
        if (dd.size() > 0) {
            for (Element mode : dd) {
                MegaMModel mCombo = new MegaMModel();
                mCombo.add(mode.text(), Integer.parseInt(mode.attr("value")));
                mCombo.setSelected(mode.select("option[selected]").hasAttr("selected"));
                m.add(mCombo);
            }
        } else {
            m = null;
        }
    }

    void setSelectedM(Elements selectedM) {
        this.selectedM = selectedM.text();
    }

    public MegaMModel getSelectedM() {
        if (pty != null) {
            for (MegaMModel sel : m) {
                if (sel.selected) {
                    return sel;
                }
            }
        }
        return null;
    }

    public List<MegaMModel> getM() {
        return m;
    }

    void setMisc() {
        misc = port.select("input[name=misc]").select("input[checked]").hasAttr("checked");
    }

    public boolean getMisc() {
        return misc;
    }

    public String getMiscVal() {
        return miscVal;
    }

    void setGR() {
        Elements dd = port.getElementsByAttributeValue("name", "gr").select("select > option");
        if (dd.size() > 0) {
            grselected = (dd.select("option[selected]").text());
            for (Element mode : dd) {
                gr.put(mode.text(), Integer.parseInt(mode.attr("value")));
            }
        } else {
            gr = null;
        }
    }

    void setHST() {
        if(!port.select("input[name=hst]").isEmpty()) {
            hst = port.select("input[name=hst]").attr("value");
        }
    }

    public String getHST() {
        return hst;
    }

    void setD() {
        if (!port.select("input[name=d]").select("input[type=checkbox]").isEmpty()) {
            d = port.select("input[name=d]").select("input[checked]").hasAttr("checked");
        } else {
            Elements dd = port.getElementsByAttributeValue("name", "d").select("select > option");
            if (dd.size() > 0) {
                for (Element mode : dd) {
                    MegaDefDModel mCombo = new MegaDefDModel();
                    mCombo.add(mode.text(), Integer.parseInt(mode.attr("value")));
                    mCombo.setSelected(mode.select("option[selected]").hasAttr("selected"));
                    defD.add(mCombo);
                }
            } else {
                defD = null;
            }
        }
    }

    public boolean getD() {
        return d;
    }

    public List<MegaDefDModel> getDefD() {
        return defD;
    }

    public MegaDefDModel getSelectedDefD() {
        if (defD != null) {
            for (MegaDefDModel sel : defD) {
                if (sel.selected) {
                    return sel;
                }
            }
        }
        return null;
    }

    void setMT() {
        if (!port.select("input[name=mt]").select("input[type=checkbox]").isEmpty()) {
            mt = port.select("input[name=mt]").select("input[checked]").hasAttr("checked");
        }
    }

    public boolean getMT() {
        return mt;
    }

    void setGRP(){
        if(!port.select("input[name=grp]").isEmpty()) {
            grp = port.select("input[name=grp]").attr("value");
        }
    }

    public String getGRP() {
        return grp;
    }

    void setDisp(){
        if(!port.select("input[name=disp]").isEmpty()) {
            disp = port.select("input[name=disp]").attr("value");
        }
    }

    public String getdisp() {
        return disp;
    }

    void setPWM() {
        if(!port.select("input[name=pwm]").attr("value").isEmpty()) {
            pwm = port.select("input[name=pwm]").attr("value");
        }
    }

    String getStatus() {
        return portStatus;
    }



    void parse() {
        port = Jsoup.parse(url);
        portStatus = port.body().text().split(" ")[1];
        digitalvalue = port.body().text().split(" ")[2];
        setPTY();
        setECMD();
        setAF();
        setEth();
        setNaf();
        setM();
        setMisc();
        setGR();
        setHST();
        setD();
        setMT();
        setGRP();
        setDisp();
        setPWM();
        log.info("parse comleted");
        //.split("<br>")[0].substring(port.body().text().split("<br>")[0].indexOf('>')+5, port.body().text().split("<br>")[0].indexOf('<'));

    }
}
