package ru.ablog.megad.configurator;

public class MegaDefDModel {
    String key;
    int value;
    boolean selected;

    public MegaDefDModel() {
    }

    @Override
    public String toString() {
        return key;
    }

    public MegaDefDModel add(String text, int value) {
        key = text;
        this.value = value;
        return this;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
