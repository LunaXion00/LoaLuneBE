package com.example.lunaproject.leaderboard.strategy.conversion;

import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.dto.BaseLeaderboardResDTO;
import com.example.lunaproject.leaderboard.entity.Leaderboard;
import org.json.simple.parser.ParseException;

public interface LeaderboardConversionStrategy {
    BaseLeaderboardResDTO convert(Leaderboard leaderboard) throws ParseException;
    GameType getGameType();
}
