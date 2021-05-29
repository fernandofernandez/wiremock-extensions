package dev.fernandofernandez.wiremock;

import dev.fernandofernandez.wiremock.model.TestValue;
import dev.fernandofernandez.wiremock.model.TestValues;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    protected TestValues createTestValues(String... ids) {
        TestValues values = new TestValues();
        List<TestValue> list = new ArrayList<>();
        values.setValues(list);
        for (int i = 0; i < ids.length; i++) {
            TestValue value = new TestValue();
            value.setId(ids[i]);
            value.setValue("encrypted_value" + (i+1));
            list.add(value);
        }
        return values;
    }
}
