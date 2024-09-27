package io.vold.ankh;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import io.vold.ankh.exception.BadRequestException;
import io.vold.ankh.exception.InternalServerException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.nonNull;

public class Context {

    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_TYPE_VALUE_TEXT_PLAIN = "text/plain";
    private static final String CONTENT_TYPE_VALUE_APPLICATION_JSON = "application/json";

    private final HttpExchange exchange;
    private final AnkhConfig config;

    private int statusCode = 200;
    private String responseBody = "";
    private String contentType = CONTENT_TYPE_VALUE_TEXT_PLAIN;

    private Map<String, String> pathParams;

    public Context(HttpExchange exchange, AnkhConfig config) {
        this.exchange = exchange;
        this.config = config;

        this.pathParams = new HashMap<>();
    }

    public String getPath() {
        return exchange.getRequestURI().getPath();
    }

    public String getMethod() {
        return exchange.getRequestMethod();
    }

    public Map<String, String> getQueryParams() {
        var queryParams = new HashMap<String, String>();
        var query = exchange.getRequestURI().getQuery();
        if (nonNull(query)) {
            for (var param : query.split("&")) {
                var entry = param.split("=");
                if (entry.length > 1) {
                    queryParams.put(entry[0], entry[1]);
                } else {
                    queryParams.put(entry[0], "");
                }
            }
        }
        return queryParams;
    }

    public String requestBodyAsString() throws IOException {
        try (var is = exchange.getRequestBody()) {
            return new String(is.readAllBytes());
        }
    }

    public <T> T json(Class<T> clazz) {
        try (var reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), UTF_8))) {
            return config.getGson().fromJson(reader, clazz);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("Invalid JSON format: " + e.getMessage());
        } catch (IOException e) {
            throw new InternalServerException("Error reading request body: " + e.getMessage());
        }
    }

    public Context status(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public Context result(String body) {
        this.responseBody = body;
        return this;
    }

    public Context json(Object obj) {
        this.responseBody = config.getGson().toJson(obj);
        this.contentType = CONTENT_TYPE_VALUE_APPLICATION_JSON;
        return this;
    }

    protected void complete() throws IOException {
        var responseBytes = responseBody.getBytes(UTF_8);
        exchange.getResponseHeaders().set(CONTENT_TYPE_HEADER, contentType);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (var os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    public void setPathParams(Map<String, String> pathParams) {
        this.pathParams = pathParams;
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public String getPathParam(String paramName) {
        return pathParams.get(paramName);
    }
}
