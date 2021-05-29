package dev.fernandofernandez.wiremock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.RequestMatcherExtension;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;

public class CustomRequestMatcher extends RequestMatcherExtension {

    private static final String MATCH_KEY = "matchkey";

    private static final String MATCH_VALUE = "matchvalue";

    private static final ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());

    @Override
    public MatchResult match(Request request, Parameters parameters) {
        if (!(parameters.containsKey(MATCH_KEY) && parameters.containsKey(MATCH_VALUE))) {
            return MatchResult.of(false);
        }
        String key = (String) parameters.get(MATCH_KEY);
        String value = (String) parameters.get(MATCH_VALUE);
        String target = "\"" + key + "\":\"" + value + "\"";
        String bodyStr = "";
        try {
            JsonNode root = mapper.readValue(request.getBody(), JsonNode.class);
            ObjectMapper objMapper = new ObjectMapper();
            bodyStr = objMapper.writeValueAsString(root);
        } catch (IOException e) {
            e.printStackTrace();
            return MatchResult.of(false);
        }
        return MatchResult.of(bodyStr.contains(target));
    }

    @Override
    public String getName() {
        return "FFCustomRequestMatcher";
    }
}
