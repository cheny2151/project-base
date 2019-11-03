package com.cheney.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Utils {

    private final static Base64.Encoder BASE_64_ENCODER = Base64.getEncoder();
    private final static Base64.Decoder BASE_64_DECODER = Base64.getDecoder();

    public static String encode(String str) {
        return BASE_64_ENCODER.encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    public static String decode(String str) {
        return new String(BASE_64_DECODER.decode(str), StandardCharsets.UTF_8);
    }

}
