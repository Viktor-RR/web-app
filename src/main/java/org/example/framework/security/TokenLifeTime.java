package org.example.framework.security;

public class TokenLifeTime {
    public static final int expirationTime = 1;

    public static long getExpirationTime() {
        return expirationTime;
    }
}