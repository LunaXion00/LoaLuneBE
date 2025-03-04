package com.example.lunaproject.game.character.utils;

import java.util.Arrays;

public enum VlrtTier {
    RADIANT("Radiant"),

    IMMORTAL_3("Immortal 3"),
    IMMORTAL_2("Immortal 2"),
    IMMORTAL_1("Immortal 1"),

    ASCENDANT_3("Ascendant 3"),
    ASCENDANT_2("Ascendant 2"),
    ASCENDANT_1("Ascendant 1"),

    DIAMOND_3("Diamond 3"),
    DIAMOND_2("Diamond 2"),
    DIAMOND_1("Diamond 1"),

    PLATINUM_3("Platinum 3"),
    PLATINUM_2("Platinum 2"),
    PLATINUM_1("Platinum 1"),

    GOLD_3("Gold 3"),
    GOLD_2("Gold 2"),
    GOLD_1("Gold 1"),

    SILVER_3("Silver 3"),
    SILVER_2("Silver 2"),
    SILVER_1("Silver 1"),

    BRONZE_3("Bronze 3"),
    BRONZE_2("Bronze 2"),
    BRONZE_1("Bronze 1"),

    IRON_3("Iron 3"),
    IRON_2("Iron 2"),
    IRON_1("Iron 1"),
    ;
    private final String tier;

    VlrtTier(String type){
        this.tier = type;
    }

    public static VlrtTier fromApiString(String apiTier) {
        return Arrays.stream(values())
                .filter(e -> e.tier.equalsIgnoreCase(apiTier.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid tier: " + apiTier));
    }
}
