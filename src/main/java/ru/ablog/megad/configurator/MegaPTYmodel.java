package ru.ablog.megad.configurator;

public class MegaPTYmodel {

    String key;
    int value;
    boolean selected;

    public MegaPTYmodel() {

    }

    public MegaPTYmodel(String text, int value) {

    }

    public MegaPTYmodel(String adc) {
        key = adc;
    }

    public MegaPTYmodel getSelected(String text) {

        return null;
    }

    @Override
    public String toString() {
        return key;
    }

    public MegaPTYmodel add(String text, int value) {
        key = text;
        this.value = value;
        return this;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
