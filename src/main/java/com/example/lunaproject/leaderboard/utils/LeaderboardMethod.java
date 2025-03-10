package com.example.lunaproject.leaderboard.utils;

import com.example.lunaproject.game.character.utils.VlrtTier;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeaderboardMethod {
    private static final Logger logger = LoggerFactory.getLogger(LeaderboardMethod.class);

    public static Double extractItemLevel(String rankingDetails){
        if (rankingDetails == null) return 0.0;
        return Double.parseDouble(rankingDetails.replace("{\"itemLevel\": ", "").replace("}", ""));
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
}
