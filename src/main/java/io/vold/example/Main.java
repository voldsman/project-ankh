package io.vold.example;

import io.vold.ankh.Ankh;
import io.vold.ankh.AnkhConfig;
import io.vold.ankh.exception.UnauthorizedException;

import java.util.Map;

import static java.util.Objects.isNull;

public class Main {

    public static void main(String[] args) {
        AnkhConfig config = new AnkhConfig();
        var server = new Ankh(9001, config);
        server.start();

        var router = server.getRouter();

        router.get("/welcome", ctx -> {
            var header = ctx.getRequestHeader("Custom");
            System.out.println("Custom header: " + header);
            ctx.setResponseHeader("From-BE", "Some-Value");
            ctx.json(Map.of());
        });

        router.get("/hello", ctx -> {
            System.out.println(ctx.getQueryParams());
            System.out.println(ctx.getQueryParams().get("myName"));

            ctx.status(200)
                    .result("Hello World");
        });

        router.get("/error", ctx -> {
            throw new IllegalArgumentException("Illegal request");
        });

        router.get("/user", ctx -> {
            ctx.status(201).json(new User("Bob"));
        });

        router.post("/user", ctx -> {
            var user = ctx.json(User.class);
            if (isNull(user)) {
                ctx.status(400).json(Map.of());
                return;
            }

            user.setName(user.getName() + "-updated");
            ctx.json(user);
        });

        router.get("/user/:userId", ctx -> {
            var userId = ctx.getPathParam("userId");
            var user = new User(userId + "-byUserId");
            ctx.json(user);
        });

        router.get("/user/:userId/:profileId", ctx -> {
            var userId = ctx.getPathParam("userId");
            var profileId = ctx.getPathParam("profileId");
            var user = new User(userId + "-byUserId, profileId-" + profileId);
            ctx.json(user);
        });

        router.get("/user/:userId/profile", ctx -> {
            var userId = ctx.getPathParam("userId");
            var user = new User(userId + "-byUserId, profile");
            ctx.json(user);
        });

        router.get("/user/profile", ctx -> {
            ctx.result("Profile endpoint");
        });

        router.get("/user/unauthorized", ctx -> {
            throw new UnauthorizedException("Unauthorized");
        });

        router.get("/custom", ctx -> {
            throw new CustomException("Custom exception");
        });

        router.exception(CustomException.class, (e, ctx) -> {
            ctx.status(500).json(e.getMessage());
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            server.stop(1);
        }));
    }

    static class User {
        private String name;

        private User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
