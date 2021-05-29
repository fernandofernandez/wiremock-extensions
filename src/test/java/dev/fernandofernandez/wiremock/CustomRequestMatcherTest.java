package dev.fernandofernandez.wiremock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import dev.fernandofernandez.wiremock.model.TestValues;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.entity.ContentType;
import org.junit.Rule;
import org.junit.Test;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class CustomRequestMatcherTest extends TestBase {

    private static final ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());

    private static final String TEST_HOST = "http://localhost:8089";

    private static final String REQUEST_MATCHER_SERVICE = "/request-matcher-service";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().options()
            .notifier(new ConsoleNotifier(true)).port(8089).extensions(CustomRequestMatcher.class));

    @Test
    public void testCustomRequestMatcher() throws IOException {
        TestValues values = createTestValues("7777777", "8888888", "9999999");
        byte[] mpValues = mapper.writeValueAsBytes(values);
        Map<String, String> headers = new HashMap<>();
        HttpEntity entity = EntityBuilder.create().setBinary(mpValues).setContentType(ContentType.APPLICATION_OCTET_STREAM).build();
        String response = invokeService(TEST_HOST + REQUEST_MATCHER_SERVICE, headers, entity);
        verify(postRequestedFor(urlEqualTo(REQUEST_MATCHER_SERVICE)));
    }

}
