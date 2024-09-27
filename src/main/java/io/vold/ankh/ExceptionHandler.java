package io.vold.ankh;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ExceptionHandler {

    private final List<ExceptionEntry<?>> handlers;
    private BiConsumer<Exception, Context> defaultHandler;

    public ExceptionHandler() {
        this.handlers = new ArrayList<>();
        this.defaultHandler = (e, ctx) -> {
            ErrorResponse errorResponse = new ErrorResponse("Internal Server Error", "An unexpected error occurred");
            ctx.status(500).json(errorResponse);
            e.printStackTrace();
        };
    }

    public <T extends Exception> void register(Class<T> exceptionClass, BiConsumer<T, Context> handler) {
        handlers.add(new ExceptionEntry<>(exceptionClass, handler));
    }

    public void setDefaultHandler(BiConsumer<Exception, Context> handler) {
        this.defaultHandler = handler;
    }

    public void handle(Exception e, Context ctx) {
        for (ExceptionEntry<?> entry : handlers) {
            if (entry.exceptionClass.isAssignableFrom(e.getClass())) {
                entry.handle(e, ctx);
                return;
            }
        }
        defaultHandler.accept(e, ctx);
    }

    private record ExceptionEntry<T extends Exception>(
            Class<T> exceptionClass,
            BiConsumer<T, Context> handler) {

        @SuppressWarnings("unchecked")
            void handle(Exception e, Context ctx) {
                handler.accept((T) e, ctx);
            }
        }
}