package ru.ablog.megad.configurator;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MegaHTTPConnect {

    private String firmwareVersion;

    public String connectToMega(String url) throws IOException {
        Logger log = LoggerFactory.getLogger(MegaHTTPConnect.class);
        String responseString;
        //log.info("url: {}", url);
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet(url);
            try (CloseableHttpResponse response = httpclient.execute(httpget)) {
                HttpEntity entity = response.getEntity();
                responseString = EntityUtils.toString(entity);
            }
        }
        return responseString;
    }
}
