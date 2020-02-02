package src.main;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class MegaHTTPConnect {
	
	private String firmwareVersion;
	public void connectToMega(String url) throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpget = new HttpGet(url);
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity);
				String r[] = responseString.split("<br>");
				
				//getting firmware version
				firmwareVersion = r[0].substring(r[0].indexOf('(')+5, r[0].indexOf(')')); 
				//System.out.println(firmwareVersion);
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
	}
	public String getFirmwareVersion() {
		return firmwareVersion;
	}
	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}
	
	
}
