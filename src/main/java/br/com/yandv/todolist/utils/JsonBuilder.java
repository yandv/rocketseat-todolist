package br.com.yandv.todolist.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonBuilder {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    private JsonObject jsonObject;

    public JsonBuilder() {
        this.jsonObject = new JsonObject();
    }

    public <T> JsonBuilder(T object) {
        this.jsonObject = GSON.toJsonTree(object).getAsJsonObject();
    }

    public JsonBuilder addProperty(String key, String value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonBuilder addProperty(String key, Number value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonBuilder addProperty(String key, Boolean value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonBuilder addProperty(String key, Character value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonBuilder add(String key, JsonElement jsonElement) {
        this.jsonObject.add(key, jsonElement);
        return this;
    }

    public JsonObject create() {
        return this.jsonObject;
    }

    public String toString() {
        return this.jsonObject.toString();
    }
}
