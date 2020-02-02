package src.main;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MegaConfig {
	String url;
	public static void main(String[] args) {
		
		Logger log = LoggerFactory.getLogger(MegaConfig.class);
		MainWindow window = new MainWindow();
		MegaHTTPConnect megaConnect = new MegaHTTPConnect();
		
		try {
			megaConnect.connectToMega("http://localhost/sec/");
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("firmware - {}", megaConnect.getFirmwareVersion());
		
		window.create();
		
	}
	

}
