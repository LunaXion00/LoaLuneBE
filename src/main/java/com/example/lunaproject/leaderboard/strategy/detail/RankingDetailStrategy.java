package com.example.lunaproject.leaderboard.strategy.detail;

import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.global.utils.GameType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface RankingDetailStrategy {
    String generateDetails(GameCharacter gameCharacter);
    Double calculateRankValue(String rankingDetails);
    GameType getGameType();
    LocalDate getRefreshDate(String rankingDetails);
}
