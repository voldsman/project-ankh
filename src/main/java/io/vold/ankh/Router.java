package io.vold.ankh;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.vold.ankh.exception.HttpException;
import io.vold.ankh.exception.NotFoundException;
import io.vold.ankh.routes.RouteTrie;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static io.vold.ankh.DevLogger.endRequest;
import static io.vold.ankh.DevLogger.startRequest;
import static io.vold.ankh.utils.ExceptionUtils.getErrorName;

public class Router implements HttpHandler {

    private final RouteTrie root;
    private final AnkhConfig config;
    private final ExceptionHandler exceptionHandler;

    public Router(AnkhConfig config) {
        this.config = config;
        this.root = new RouteTrie();
        this.exceptionHandler = new ExceptionHandler();

        setupDefaultExceptionHandlers();
    }

    public void get(String path, Consumer<Context> handler) {
        addRoute("GET", path, handler);
    }

    public void post(String path, Consumer<Context> handler) {
        addRoute("POST", path, handler);
    }

    public void put(String path, Consumer<Context> handler) {
        addRoute("PUT", path, handler);
    }

    public void delete(String path, Consumer<Context> handler) {
        addRoute("DELETE", path, handler);
    }

    private void addRoute(String method, String path, Consumer<Context> handler) {
        root.insert(method, path, handler);
    }

    public <T extends Exception> void exception(Class<T> exceptionClass, BiConsumer<T, Context> handler) {
        exceptionHandler.register(exceptionClass, handler);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var path = exchange.getRequestURI().getPath();
        var method = exchange.getRequestMethod();
        var logContext = startRequest(method, path);

        try {
            var result = root.find(method, path);
            var context = new Context(exchange, config);

            try {
                if (result != null) {
                    context.setPathParams(result.pathParams);
                    result.handler.accept(context);
                } else {
                    throw new NotFoundException("Route not found: " + method + " " + path);
                }
            } catch (Exception e) {
                exceptionHandler.handle(e, context);
            } finally {
                context.complete();
            }
        } finally {
            endRequest(logContext);
        }
    }

    private void setupDefaultExceptionHandlers() {
        exceptionHandler.register(HttpException.class, (e, ctx) -> {
            var errorResponse = new ErrorResponse(getErrorName(e.getStatusCode()), e.getMessage());
            ctx.status(e.getStatusCode()).json(errorResponse);
        });

        exceptionHandler.setDefaultHandler((e, ctx) -> {
            var errorResponse = new ErrorResponse("Internal Server Error", "An unexpected error occurred: " + e.getMessage());
            ctx.status(500).json(errorResponse);
            e.printStackTrace();
        });
    }
}
