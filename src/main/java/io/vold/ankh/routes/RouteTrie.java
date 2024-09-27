package io.vold.ankh.routes;

import io.vold.ankh.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RouteTrie {

    private final Map<String, TrieNode> roots;

    public RouteTrie() {
        this.roots = new HashMap<>();
    }

    public void insert(String method, String path, Consumer<Context> handler) {
        TrieNode node = roots.computeIfAbsent(method, k -> new TrieNode());
        String[] parts = path.split("/");
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (part.startsWith(":")) {
                node = node.children.computeIfAbsent("*", k -> new TrieNode());
                node.paramName = part.substring(1);
            } else {
                node = node.children.computeIfAbsent(part, k -> new TrieNode());
            }
        }
        node.handler = handler;
    }

    public MatchResult find(String method, String path) {
        TrieNode node = roots.get(method);
        if (node == null) return null;

        String[] parts = path.split("/");
        Map<String, String> pathParams = new HashMap<>();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            TrieNode child = node.children.get(part);
            if (child == null) {
                child = node.children.get("*");
                if (child == null) return null;
                pathParams.put(child.paramName, part);
            }
            node = child;
        }
        return node.handler != null ? new MatchResult(node.handler, pathParams) : null;
    }
}
