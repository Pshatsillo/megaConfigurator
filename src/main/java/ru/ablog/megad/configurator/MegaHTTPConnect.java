package ru.ablog.megad.configurator;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MegaHTTPConnect {
	
	private String firmwareVersion;
	public String connectToMega(String url) throws ClientProtocolException, IOException {
		Logger log = LoggerFactory.getLogger(MegaHTTPConnect.class);
		String responseString;
		log.info("url: {}", url);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpget = new HttpGet(url);
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				HttpEntity entity = response.getEntity();
				responseString = EntityUtils.toString(entity);
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
		return responseString;
	}
}
