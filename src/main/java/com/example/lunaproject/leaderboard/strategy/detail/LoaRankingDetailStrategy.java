package com.example.lunaproject.leaderboard.strategy.detail;

import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.game.character.entity.LoaCharacter;
import com.example.lunaproject.game.character.repository.LoaCharacterRepository;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.strategy.detail.RankingDetailStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.example.lunaproject.leaderboard.utils.LeaderboardMethod.extractItemLevel;

@Component
@RequiredArgsConstructor
public class LoaRankingDetailStrategy implements RankingDetailStrategy {
    private final LoaCharacterRepository loaCharacterRepository;
    @Override
    public String generateDetails(GameCharacter gameCharacter) {
        LoaCharacter mainCharacter = loaCharacterRepository.findById(gameCharacter.getId()).orElse(null);
        return "{\"itemLevel\": " + mainCharacter.getItemLevel() + "}";
    }

    @Override
    public Double calculateRankValue(String rankingDetails) {
        return extractItemLevel(rankingDetails);
    }

    @Override
    public GameType getGameType() {
        return GameType.lostark;
    }
}
