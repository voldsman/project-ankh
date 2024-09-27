package io.vold.ankh;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AnkhConfig {

    private Gson gson;

    public AnkhConfig() {
        this.gson = new GsonBuilder().create();
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public Gson getGson() {
        return gson;
    }
}
