package dev.fernandofernandez.wiremock;

import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.RequestMatcherExtension;

public class CustomRequestMatcher extends RequestMatcherExtension {

    @Override
    public MatchResult match(Request request, Parameters parameters) {
        return null;
    }
}
