package io.github.martinez1337.hmac.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonFactory {
    public static Gson createDefault() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }
}
