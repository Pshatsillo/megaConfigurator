package ru.ablog.megad.configurator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ablog.megad.configurator.windows.MegaMainScreen;

import java.util.HashMap;

public class MegaDPortModel {
    Logger log = LoggerFactory.getLogger(MegaDPortModel.class);
    String url;
    Document port;
    String setSelectedPTY;
    String portStatus;
    HashMap<String, Integer> pn = new HashMap<String, Integer>();
    String ecmdInput;
    String eth;
    boolean naf;
    HashMap<String, Integer> m = new HashMap<String, Integer>();
    String selectedM;
    boolean misc;
    boolean d;

    public String getEth() {
        return eth;
    }

    public void setEth() {
        eth  = port.select("input[name=eth]").attr("value");
    }

    public boolean isNaf() {
        return naf;

    }

    public void setNaf() {
        if (port.select("checkbox[name=naf]").hasAttr("checked")){
            naf = true;
        } else {
            naf = false;
        }
    }

    public HashMap<String, Integer> getM() {
        return m;
    }

    public void setM() {
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

    public String getSelectedM() {
        return selectedM;
    }

    public void setSelectedM(Elements selectedM) {
        this.selectedM = selectedM.text();
    }

    public boolean isMisc() {
        return misc;
    }

    public void setMisc() {
       Object chb = port.select("input[name=misc]");
        if (port.select("checkbox[name=misc]").hasAttr("checked")){
            misc = true;
        } else {
            misc = false;
        }
    }

    public boolean isD() {
        return d;
    }

    public void setD() {
        if (port.select("checkbox[name=d]").hasAttr("checked")){
            d = true;
        } else {
            d = false;
        }
    }

    public MegaDPortModel(String connectToMega) {
        url = connectToMega;
        parse();
        getPTY();
    }



    void setPTY() {
        Elements dd = port.getElementsByAttributeValue("name", "pty").select("select > option");
        if (dd.size() > 0) {
            setSelectedPTY(dd.select("option[selected]"));
            for (Element mode : dd) {
                pn.put(mode.text(), Integer.parseInt(mode.attr("value")));
            }
        } else {
            pn = null;
        }
    }

    HashMap<String, Integer> getPTY() {
        return pn;
    }

    void setSelectedPTY(Elements selected) {
        setSelectedPTY = selected.text();
    }

    String getStatus() {
        return portStatus;
    }

    void setECMD() {
        ecmdInput = port.select("input[name=ecmd]").attr("value");
    }

    String getECMD(){
        return ecmdInput;
    }

    void parse() {
        port = Jsoup.parse(url);
        portStatus = port.body().text().split(" ")[1];
        setPTY();
        setECMD();
        setNaf();
        setEth();
        setM();
        setMisc();
        setD();

        log.info("parse comleted");
        //.split("<br>")[0].substring(port.body().text().split("<br>")[0].indexOf('>')+5, port.body().text().split("<br>")[0].indexOf('<'));

    }
}
