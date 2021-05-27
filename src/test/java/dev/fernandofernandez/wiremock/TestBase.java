package dev.fernandofernandez.wiremock;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

public abstract class TestBase {

    protected String invokeService(String serviceUrl, Map<String, String> headers, HttpEntity entity) throws IOException {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(serviceUrl);
            for (String name : headers.keySet()) {
                httpPost.addHeader(name, headers.get(name));
            }

            httpPost.setEntity(entity);
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String respStr = EntityUtils.toString(response.getEntity());
                return respStr;
            }
        }
    }
}
