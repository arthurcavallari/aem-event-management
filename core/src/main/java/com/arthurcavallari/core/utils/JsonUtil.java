package com.arthurcavallari.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {
    private static final Gson gson = new GsonBuilder().create();

    public static String toJson(Object object) {
        return gson.toJson(object);
    }
}
