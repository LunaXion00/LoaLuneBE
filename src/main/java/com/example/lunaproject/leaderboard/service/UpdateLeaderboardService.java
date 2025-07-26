package com.example.lunaproject.leaderboard.service;

import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.strategy.detail.RankingDetailStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateLeaderboardService {
    private final LeaderboardUpdaterService leaderboardUpdaterService;
    private final Map<GameType, RankingDetailStrategy> rankingStrategies;
    private final Set<GameType> refreshingGames = ConcurrentHashMap.newKeySet();

    public enum RefreshResult {
        UPDATED,
        IN_PROGRESS,
        UNSUPPORTED_GAME
    }

    @Scheduled(cron = "00 00 15 * * *")
    public void updateAllLeaderboards() {
        log.info("모든 게임의 리더보드 업데이트 시작...");
        for (GameType type : rankingStrategies.keySet()) {
            try {
                if (refreshLeaderboard(type) == RefreshResult.UPDATED) {
                    log.info("{} 리더보드 업데이트 완료", type);
                } else {
                    log.info("{} 리더보드 갱신이 이미 진행 중입니다.", type);
                }
            } catch (Exception e) {
                log.error("{} 리더보드 갱신에 실패했습니다.", type, e);
            }
        }
    }

    public RefreshResult refreshLeaderboard(GameType gameType) {
        if (!rankingStrategies.containsKey(gameType)) {
            return RefreshResult.UNSUPPORTED_GAME;
        }
        if (!refreshingGames.add(gameType)) {
            return RefreshResult.IN_PROGRESS;
        }

        try {
            leaderboardUpdaterService.updateLeaderboard(gameType);
            return RefreshResult.UPDATED;
        } finally {
            refreshingGames.remove(gameType);
        }
    }
}
