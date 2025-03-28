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
    public static LocalDate extractRefreshDate(String rankingDetails) throws ParseException {
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(rankingDetails);
            String dateStr = (String) json.get("refreshDate");
            // 여러 날짜 형식 지원
            DateTimeFormatter[] formatters = {
                    DateTimeFormatter.ISO_LOCAL_DATE,
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
            };

            for (DateTimeFormatter formatter : formatters) {
                try {
                    if (formatter.equals(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) {
                        // LocalDateTime으로 파싱 후 LocalDate로 변환
                        return LocalDateTime.parse(dateStr, formatter).toLocalDate();
                    } else {
                        return LocalDate.parse(dateStr, formatter);
                    }
                } catch (DateTimeParseException ignored) {
                    // 다음 포맷 시도
                }
            }

            throw new IllegalArgumentException("지원되지 않는 날짜 형식: " + dateStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException("JSON 파싱 실패: " + rankingDetails, e);
        }
    }
}
