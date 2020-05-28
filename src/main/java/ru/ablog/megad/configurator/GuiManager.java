package ru.ablog.megad.configurator;

import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Window;

public class GuiManager {

    private OnGUIUpdate update;

    public void registerOnGUIUpdateEventListener(OnGUIUpdate guiUpdate)
    {
        this.update = guiUpdate;
    }

    public void refreshWindow(Component win){
        update.updateGUI(win);
    }

}
