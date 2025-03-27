package com.example.lunaproject.leaderboard.utils;

import com.example.lunaproject.game.character.utils.VlrtTier;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LeaderboardMethod {
    private static final Logger logger = LoggerFactory.getLogger(LeaderboardMethod.class);

    public static Double extractItemLevel(String rankingDetails) throws ParseException {
        if (rankingDetails == null) return 0.0;
        JSONObject json = (JSONObject) new JSONParser().parse(rankingDetails);
        Number itemLevel = (Number) json.get("itemLevel");
        double level = itemLevel.doubleValue();
        return level;
    }
    public static Double extractVlrtRr(String rankingDetails) throws ParseException {
        if(rankingDetails == null) return 0.0;
        JSONObject json = (JSONObject) new JSONParser().parse(rankingDetails);
        String tierName = (String) json.get("tier");
        Number rrNumber = (Number) json.get("rr");
        double rr = rrNumber.doubleValue();
        VlrtTier tier = VlrtTier.fromApiString(tierName);
        return tier.getRankValue()*100+rr;
    }
    public static LocalDateTime extractRefreshDate(String rankingDetails) throws ParseException {
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(rankingDetails);
            String dateStr = (String) json.get("refreshDate"); // 문자열로 먼저 추출

            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            return LocalDateTime.parse(dateStr, formatter);
        } catch (ParseException | DateTimeParseException e) {
            throw new IllegalArgumentException("날짜 파싱 실패: " + rankingDetails, e);
        }
    }
}
