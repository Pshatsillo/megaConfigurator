package ru.ablog.megad.configurator;

public class MegaMModel {

    String key;
    int value;
    boolean selected;

    @Override
    public String toString() {
        return key;
    }

    public MegaMModel add(String text, int value) {
        key = text;
        this.value = value;
        return this;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getSelectedValue() {
        return value;
    }
}
