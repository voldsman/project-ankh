package io.vold.ankh;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import static com.sun.net.httpserver.HttpServer.create;
import static io.vold.ankh.utils.ServerUtils.checkJavaVersion;
import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;

public class Ankh {

    private final HttpServer server;
    private final Router router;
    private final AnkhConfig config;

    public Ankh(int port) {
        this(port, new AnkhConfig());
    }

    public Ankh(int port, AnkhConfig config) {
        checkJavaVersion();

        try {
            this.server = create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            // todo: introduce server exceptions
            throw new RuntimeException(e);
        }

        this.router = new Router(config);
        this.config = config;
        this.server.setExecutor(newVirtualThreadPerTaskExecutor());
    }

    public Router getRouter() {
        return router;
    }

    public void start() {
        server.createContext("/", router);
        server.start();
        System.out.println("Ankh Server started on port " + server.getAddress().getPort());
    }

    public void stop(int delay) {
        server.stop(delay);
        System.out.println("Ankh Server stopped");
    }
}
