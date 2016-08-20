package com.auth0.lock.react.bridge.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class Randomizer {

    private final Random random;
    private final SimpleDateFormat sdf;

    public Randomizer() {
        this.random = new Random();
        this.sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        this.sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public String string() {
        byte[] bytes = new byte[255];
        random.nextBytes(bytes);
        return new String(bytes);
    }

    public int integer() {
        return random.nextInt();
    }

    public String isoDate() {
        Date date = new Date(random.nextInt());
        return sdf.format(date);
    }
}
