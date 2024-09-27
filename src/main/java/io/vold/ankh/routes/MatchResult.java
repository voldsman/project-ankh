package io.vold.ankh.routes;

import io.vold.ankh.Context;

import java.util.Map;
import java.util.function.Consumer;

public class MatchResult {

    public Consumer<Context> handler;
    public Map<String, String> pathParams;

    MatchResult(Consumer<Context> handler, Map<String, String> pathParams) {
        this.handler = handler;
        this.pathParams = pathParams;
    }
}
