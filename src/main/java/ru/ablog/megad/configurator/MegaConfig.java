package ru.ablog.megad.configurator;

import ru.ablog.megad.configurator.interfaces.OnGUIUpdate;
import ru.ablog.megad.configurator.utils.NetworkUtils;
import ru.ablog.megad.configurator.windows.MainWindow;

public class MegaConfig {
public static String currentIp;
	public static void main(String[] args) {
		GuiManager gm = new GuiManager();
		OnGUIUpdate gu = new GenGUI();
		gm.registerOnGUIUpdateEventListener(gu);
		NetworkUtils interfaces = new NetworkUtils();
		MainWindow window = new MainWindow();
		gm.refreshWindow(window.create(interfaces.getLocalInterfaces()));
	}
}
