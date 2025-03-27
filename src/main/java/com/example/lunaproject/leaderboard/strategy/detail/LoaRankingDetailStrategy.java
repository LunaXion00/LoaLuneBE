package com.example.lunaproject.leaderboard.strategy.detail;

import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.game.character.entity.LoaCharacter;
import com.example.lunaproject.game.character.repository.LoaCharacterRepository;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.strategy.detail.RankingDetailStrategy;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.example.lunaproject.leaderboard.utils.LeaderboardMethod.extractItemLevel;
import static com.example.lunaproject.leaderboard.utils.LeaderboardMethod.extractRefreshDate;

@Component
@RequiredArgsConstructor
public class LoaRankingDetailStrategy implements RankingDetailStrategy {
    private final LoaCharacterRepository loaCharacterRepository;
    @Override
    public String generateDetails(GameCharacter gameCharacter) {
        LoaCharacter mainCharacter = loaCharacterRepository.findById(gameCharacter.getId()).orElse(null);
        return String.format("{\"itemLevel\": %f, \"refreshDate\": \"%s\"}",
                mainCharacter.getItemLevel(),
                LocalDateTime.now());
    }

    @Override
    public Double calculateRankValue(String rankingDetails) {
        try {
            return extractItemLevel(rankingDetails);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameType getGameType() {
        return GameType.lostark;
    }

    @Override
    public LocalDateTime getRefreshDate(String rankingDetails) {
        try {
            return extractRefreshDate(rankingDetails);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
