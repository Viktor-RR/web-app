package org.example.framework.security;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TokenLifeTime {
    public static final int expirationTime = 1;

    public static long getExpirationTime() {
        return expirationTime;
    }
}
