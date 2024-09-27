package io.vold.ankh.routes;

import io.vold.ankh.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TrieNode {

    Map<String, TrieNode> children = new HashMap<>();
    Consumer<Context> handler;
    String paramName;
}
