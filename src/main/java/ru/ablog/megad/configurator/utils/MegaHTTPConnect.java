package ru.ablog.megad.configurator.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class MegaHTTPConnect {
    public String connectToMega(String url) throws IOException {
        String responseString;
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            int CONNECTION_TIMEOUT_MS = 500; // Timeout in millis.
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
                    .setConnectTimeout(CONNECTION_TIMEOUT_MS)
                    .setSocketTimeout(CONNECTION_TIMEOUT_MS)
                    .build();

            HttpGet httpget = new HttpGet(url);
            httpget.setConfig(requestConfig);
            try (CloseableHttpResponse response = httpclient.execute(httpget)) {
                HttpEntity entity = response.getEntity();
                responseString = EntityUtils.toString(entity);
            }
        }
        return responseString;
    }
}
