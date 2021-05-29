package dev.fernandofernandez.wiremock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import dev.fernandofernandez.wiremock.model.TestValue;
import dev.fernandofernandez.wiremock.model.TestValues;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;

public class CustomResponseTransformerTest extends TestBase {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String TEST_HOST = "http://localhost:8089";

    private static final String RESPONSE_TRANSFORMER_SERVICE = "/response-transformer-service";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().options()
            .notifier(new ConsoleNotifier(true)).port(8089).extensions(CustomResponseTransformer.class));

    @Test
    public void testResponseTransformationSameSize() throws IOException {
        TestValues values = createTestValues("7777777", "8888888", "9999999");
        String testStr = mapper.writeValueAsString(values);
        Map<String, String> headers = new HashMap<>();
        StringEntity entity = new StringEntity(testStr, ContentType.APPLICATION_JSON);
        String response = invokeService(TEST_HOST + RESPONSE_TRANSFORMER_SERVICE, headers, entity);
        verify(postRequestedFor(urlEqualTo(RESPONSE_TRANSFORMER_SERVICE)));
        TestValues result = mapper.readValue(response, TestValues.class);
        assertEquals(3, result.getValues().size());
        assertEquals("7777777", result.getValues().get(0).getId());
        assertEquals("decrypted_value1", result.getValues().get(0).getValue());
        assertEquals("8888888", result.getValues().get(1).getId());
        assertEquals("decrypted_value2", result.getValues().get(1).getValue());
        assertEquals("9999999", result.getValues().get(2).getId());
        assertEquals("decrypted_value3", result.getValues().get(2).getValue());
    }

    @Test
    public void testResponseTransformationRequestGreaterThan() throws IOException {
        TestValues values = createTestValues("7777777", "8888888", "9999999", "AAAAAAA", "BBBBBBB");
        String testStr = mapper.writeValueAsString(values);
        Map<String, String> headers = new HashMap<>();
        StringEntity entity = new StringEntity(testStr, ContentType.APPLICATION_JSON);
        String response = invokeService(TEST_HOST + RESPONSE_TRANSFORMER_SERVICE, headers, entity);
        verify(postRequestedFor(urlEqualTo(RESPONSE_TRANSFORMER_SERVICE)));
        TestValues result = mapper.readValue(response, TestValues.class);
        assertEquals(5, result.getValues().size());
        assertEquals("7777777", result.getValues().get(0).getId());
        assertEquals("decrypted_value1", result.getValues().get(0).getValue());
        assertEquals("8888888", result.getValues().get(1).getId());
        assertEquals("decrypted_value2", result.getValues().get(1).getValue());
        assertEquals("9999999", result.getValues().get(2).getId());
        assertEquals("decrypted_value3", result.getValues().get(2).getValue());
        assertEquals("AAAAAAA", result.getValues().get(3).getId());
        assertEquals("decrypted_value3", result.getValues().get(3).getValue());
        assertEquals("BBBBBBB", result.getValues().get(4).getId());
        assertEquals("decrypted_value3", result.getValues().get(4).getValue());
    }

    @Test
    public void testResponseTransformationRequestLessThan() throws IOException {
        TestValues values = createTestValues("7777777");
        String testStr = mapper.writeValueAsString(values);
        Map<String, String> headers = new HashMap<>();
        StringEntity entity = new StringEntity(testStr, ContentType.APPLICATION_JSON);
        String response = invokeService(TEST_HOST + RESPONSE_TRANSFORMER_SERVICE, headers, entity);
        verify(postRequestedFor(urlEqualTo(RESPONSE_TRANSFORMER_SERVICE)));
        TestValues result = mapper.readValue(response, TestValues.class);
        assertEquals(1, result.getValues().size());
        assertEquals("7777777", result.getValues().get(0).getId());
        assertEquals("decrypted_value1", result.getValues().get(0).getValue());
    }

}
