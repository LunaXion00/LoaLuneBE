package com.example.lunaproject.leaderboard.strategy.conversion;

import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.strategy.conversion.LeaderboardConversionStrategy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConversionStrategyRegistry {
    private final List<LeaderboardConversionStrategy> strategies;
    private Map<GameType, LeaderboardConversionStrategy> strategyMap;

    @PostConstruct
    void init() {
        strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        LeaderboardConversionStrategy::getGameType,
                        Function.identity()
                ));
    }

    public LeaderboardConversionStrategy getStrategy(GameType gameType) {
        return Optional.ofNullable(strategyMap.get(gameType))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported game type"));
    }
}