package dev.fernandofernandez.wiremock;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;

public class CustomResponseTransformer extends ResponseTransformer {

    @Override
    public Response transform(Request request, Response response, FileSource files, Parameters parameters) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
