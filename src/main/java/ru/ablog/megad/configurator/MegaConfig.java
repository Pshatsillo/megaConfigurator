package ru.ablog.megad.configurator;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ablog.megad.configurator.windows.MegaSelectDeviceScreen;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class MegaConfig {
	private static DatagramSocket udpSocket;
	String url;
	static Logger log = LoggerFactory.getLogger(MegaConfig.class);
	static InetAddress address;
	static MegaDUDPRequests udpServer;
	public static GuiManager gm;

	public static void main(String[] args) throws IOException {

		//DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
		//startupScreen = new MegaStartupScreen();
		//Screen screen = null;
		//screen = terminalFactory.createScreen();
		//screen.startScreen();

		gm = new GuiManager();
		OnGUIUpdate gu = new genGUI();
		gm.registerOnGUIUpdateEventListener(gu);
		MainWindow window = new MainWindow();
		gm.refreshWindow(window.create(getLocaInterfaces()));
		//udpServer.start();
		//address = getLocaInterfaces().get(3);
		//window.create(getLocaInterfaces());
		//address = getLocaInterfaces().get(3);


		//MegaHTTPConnect megaConnect = new MegaHTTPConnect();
		//megaConnect.connectToMega("http://192.168.10.19/sec/");
		//log.info("firmware - {}", megaConnect.getFirmwareVersion());
		//log.info("firmware - {}", getLocaInterfaces().get(3).toString());
		//String ch = "sec";								//preamble  mode     pass(sec)     old-ip       new-ip
		//String hex = String.format("%02x", (int) ch); // aa 00     04  73 65 63 00 00   c0 a8 00 0e  c0 a8 0a 13
		//log.info("char - {}", ch.getBytes());
		//DatagramSocket socket;
		//InetAddress address;
		//socket = new DatagramSocket();
		//byte[]buf = {(byte) 170, 0, 12};
		//DatagramPacket packet
		//		= new DatagramPacket(buf, buf.length, InetAddress.getByName(createBroadcastInterface(getLocaInterfaces().get(3))), 52000);
		//socket.send(packet);*//*


		//Thread.sleep(10000);
		//udpServer.interrupt();
		//MegaDUDPRequests.running = false;*/
		
	}

	public static ArrayList<InetAddress> getLocaInterfaces() throws SocketException {
		ArrayList<InetAddress> listIp = new ArrayList<InetAddress>();
		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
			NetworkInterface ip = en.nextElement();
			for (Enumeration<InetAddress> enumIpAddr = ip.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
				InetAddress ipv4 = enumIpAddr.nextElement();
				if(ipv4 instanceof Inet4Address) {
					listIp.add(ipv4);
					//log.info(ipv4.getHostAddress());
				}
			}
		}
		return listIp;
	}

	public static String createBroadcastInterface(InetAddress ip){
		String[] ipArray = ip.getHostAddress().split("[.]");
		return String.format("%s.%s.%s.255", ipArray[0], ipArray[1], ipArray[2]);
	}

	public static void startUDPserver(InetAddress address) throws SocketException {
		udpServer = new MegaDUDPRequests(address);
		//OnUDPIncomingEventListener megaSetupListener = new MegaSetup();
		OnUDPIncomingEventListener megaMainWindowListener = new MainWindow();
		//udpServer.registerOnGeekEventListener(megaSetupListener);
		udpServer.registerOnGeekEventListener(megaMainWindowListener);
		udpServer.startUDPServer();
	}

	public static void stopUDPServer() throws SocketException {
		udpServer.stop();
	}


	public static void registerDeviceListener(MegaSelectDeviceScreen megaSelectDeviceScreen) {
		OnUDPIncomingEventListener megaSelectDeviceScreenListener = megaSelectDeviceScreen;
		udpServer.registerOnGeekEventListener(megaSelectDeviceScreenListener);
	}
}
