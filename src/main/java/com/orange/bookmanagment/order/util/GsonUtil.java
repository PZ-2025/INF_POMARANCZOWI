package com.orange.bookmanagment.order.util;

import com.google.gson.*;

import java.lang.reflect.Type;

public class GsonUtil {

    private static final GsonBuilder gsonBuilder;
    private static final Gson gson;

    static {
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    public static JsonElement toJsonTree(Object src) {
        return gson.toJsonTree(src);
    }

    public static JsonElement toJsonTree(Object src, Type typeOfSrc) {
        return gson.toJsonTree(src, typeOfSrc);
    }

    public static <T> T fromJson(JsonElement json, Class<T> classOfT) throws JsonSyntaxException {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Class<T> classOfT){
        return gson.fromJson(json, classOfT);
    }

    public static String toJson(Object src) {
        return gson.toJson(src);
    }
}
