package com.example.lunaproject.game.character.utils;

import com.example.lunaproject.leaderboard.service.UpdateLeaderboardService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

@Getter
public enum VlrtTier {
    RADIANT("RADIANT", 24),

    IMMORTAL_3("IMMORTAL_3", 23),
    IMMORTAL_2("IMMORTAL_2", 22),
    IMMORTAL_1("IMMORTAL_1", 21),

    ASCENDANT_3("ASCENDANT_3", 20),
    ASCENDANT_2("ASCENDANT_2", 19),
    ASCENDANT_1("ASCENDANT_1", 18),

    DIAMOND_3("DIAMOND_3", 17),
    DIAMOND_2("DIAMOND_2",16),
    DIAMOND_1("DIAMOND_1", 15),

    PLATINUM_3("PLATINUM_3", 14),
    PLATINUM_2("PLATINUM_2", 13),
    PLATINUM_1("PLATINUM_1", 12),

    GOLD_3("GOLD_3", 11),
    GOLD_2("GOLD_2", 10),
    GOLD_1("GOLD_1", 9),

    SILVER_3("SILVER_3", 8),
    SILVER_2("SILVER_2", 7),
    SILVER_1("SILVER_1", 6),

    BRONZE_3("BRONZE_3", 5),
    BRONZE_2("BRONZE_2", 4),
    BRONZE_1("BRONZE_1", 3),

    IRON_3("IRON_3", 2),
    IRON_2("IRON_2", 1),
    IRON_1("IRON_1", 0),
    ;
    private final String tier;
    private final int rankValue;

    VlrtTier(String type, int rankValue){
        this.tier = type;
        this.rankValue = rankValue;
    }

    public static VlrtTier fromApiString(String apiTier) {
        final Logger logger = LoggerFactory.getLogger(VlrtTier.class);
        return Arrays.stream(values())
                .filter(e -> e.tier.equalsIgnoreCase(apiTier.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid tier: " + apiTier));
    }
}
