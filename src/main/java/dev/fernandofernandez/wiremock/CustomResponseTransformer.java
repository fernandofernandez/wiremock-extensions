package dev.fernandofernandez.wiremock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;

public class CustomResponseTransformer extends ResponseTransformer {

    private static final String VALUES = "values";

    private static final String ID = "id";

    private static final String VALUE = "value";

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean applyGlobally() {
        return false;
    }

    @Override
    public Response transform(Request request, Response response, FileSource files, Parameters parameters) {
        String requestBody = request.getBodyAsString();
        String responseBody = response.getBodyAsString();
        try {
            JsonNode reqRoot = mapper.readTree(requestBody);
            JsonNode respRoot = mapper.readTree(responseBody);
            JsonNode reqValues = reqRoot.get(VALUES);
            ArrayNode respValues = (ArrayNode) respRoot.get(VALUES);
            if (reqValues != null && reqValues.isArray() && respValues != null && respValues.isArray() && respValues.size() > 0) {
                int i = 0;
                ObjectNode respItem;
                do {
                    respItem = (ObjectNode) respValues.get(i);
                    if (i < reqValues.size()) {
                        JsonNode reqItem = reqValues.get(i);
                        JsonNode idNode = reqItem.get(ID);
                        String id = idNode.asText();
                        respItem.remove(ID);
                        respItem.put(ID, id);
                        i++;
                    } else {
                        respValues.remove(i);
                    }
                } while(i < respValues.size());
                JsonNode valueNode = respItem.get(VALUE);
                String value = valueNode.asText();
                for (; i < reqValues.size(); i++) {
                    JsonNode reqItem = reqValues.get(i);
                    JsonNode idNode = reqItem.get(ID);
                    String id = idNode.asText();
                    ObjectNode newRespItem = mapper.createObjectNode();
                    newRespItem.put(ID, id);
                    newRespItem.put(VALUE, value);
                    respValues.add(newRespItem);
                }
                responseBody = mapper.writeValueAsString(respRoot);
            } else {
                System.out.println("'values' array not found! Using matched response as is.");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return Response.Builder.like(response).body(responseBody).build();
    }

    @Override
    public String getName() {
        return "FFCustomResponseTransformer";
    }
}
