package com.example.lunaproject.leaderboard.strategy;

import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.dto.BaseLeaderboardResDTO;
import com.example.lunaproject.leaderboard.entity.Leaderboard;

public interface LeaderboardConversionStrategy {
    BaseLeaderboardResDTO convert(Leaderboard leaderboard);
    GameType getGameType();
}
