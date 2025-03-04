package com.example.lunaproject.global.utils;

import java.util.Arrays;

public enum GameServer {
    AP("ap"),
    KR("kr"),
    NA("na"),
    EU("eu");

    private final String regionCode;

    GameServer(String regionCode) {
        this.regionCode = regionCode;
    }

    public static GameServer fromRegion(String region) {
        return Arrays.stream(values())
                .filter(s -> s.regionCode.equalsIgnoreCase(region))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid region: " + region));
    }
}
