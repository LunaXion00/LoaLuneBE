package com.example.lunaproject.leaderboard.strategy.detail;

import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.game.character.entity.VlrtAccount;
import com.example.lunaproject.game.character.repository.VlrtAccountRepository;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.service.UpdateLeaderboardService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.lunaproject.leaderboard.utils.LeaderboardMethod.extractRefreshDate;
import static com.example.lunaproject.leaderboard.utils.LeaderboardMethod.extractVlrtRr;

@Component
@RequiredArgsConstructor
public class VlrtRankingDetailStrategy implements RankingDetailStrategy{
    private final VlrtAccountRepository vlrtAccountRepository;

    @Override
    public String generateDetails(GameCharacter gameCharacter) {
        VlrtAccount mainCharacter = vlrtAccountRepository.findById(gameCharacter.getId()).orElse(null);
        return String.format("""
        {
            "tier": "%s",
            "rr": %d,
            "refreshDate" : "%s"
        }
        """,
                mainCharacter.getTier().name(), // 쌍따옴표로 감싸짐 ✅
                mainCharacter.getRr(),
                LocalDate.now()
        ).replaceAll("\\s+", "");
    }

    @Override
    public Double calculateRankValue(String rankingDetails) {
        try {
            return extractVlrtRr(rankingDetails);
        } catch (org.json.simple.parser.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameType getGameType() {
        return GameType.vlrt;
    }

    @Override
    public LocalDate getRefreshDate(String rankingDetails) {
        try {
            return extractRefreshDate(rankingDetails);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
