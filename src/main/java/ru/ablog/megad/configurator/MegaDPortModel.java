package ru.ablog.megad.configurator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class MegaDPortModel {
    Logger log = LoggerFactory.getLogger(MegaDPortModel.class);
    String url;
    Document port;
    String portStatus;
    String digitalvalue;
    HashMap<String, Integer> pty = new HashMap<>();
    String selectedPTY;
    String ecmd;
    boolean af;
    String eth;
    boolean naf;
    HashMap<String, Integer> m = new HashMap<>();
    String selectedM;
    boolean misc;
    String miscVal;
    String grselected;
    HashMap<String, Integer> gr = new HashMap<>();
    String hst;
    boolean d;
    HashMap<String, Integer> defD = new HashMap<>();
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
            setSelectedPTY(dd.select("option[selected]"));
            for (Element mode : dd) {
                pty.put(mode.text(), Integer.parseInt(mode.attr("value")));
            }
        } else {
            pty = null;
        }
    }

    void setSelectedPTY(Elements selected) {
        selectedPTY = selected.text();
    }

    public String getSelectedPTY(){
        if(selectedPTY != null) {
            return selectedPTY;
        } else return "ADC";
    }

    public HashMap<String, Integer> getPTY() {
        return pty;
    }

    void setECMD() {
        if(!port.select("input[name=ecmd]").isEmpty()) {
            ecmd = port.select("input[name=ecmd]").attr("value");
        }
    }

    public String getECMD(){
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
            setSelectedM(dd.select("option[selected]"));
            for (Element mode : dd) {
                m.put(mode.text(), Integer.parseInt(mode.attr("value")));
            }
        } else {
            m = null;
        }
    }

    void setSelectedM(Elements selectedM) {
        this.selectedM = selectedM.text();
    }

    public String getSelectedM() {
        return selectedM;
    }
    public HashMap<String, Integer> getM() {
        return m;
    }

    void setMisc() {
        Elements e = port.select("input[name=misc]").select("input[type=checkbox]");
        if (!port.select("input[name=misc]").select("input[type=checkbox]").isEmpty()) {
            misc = port.select("input[name=misc]").select("input[checked]").hasAttr("checked");
        } else if(!port.select("input[name=misc]").isEmpty()){
            miscVal = port.select("input[name=misc]").attr("value");
        }
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
                selectedDefD = dd.select("option[selected]").text();
                for (Element mode : dd) {
                    defD.put(mode.text(), Integer.parseInt(mode.attr("value")));
                }
            } else {
                defD = null;
            }
        }
    }

    public boolean getD() {
        return d;
    }

    void setMT(){
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
