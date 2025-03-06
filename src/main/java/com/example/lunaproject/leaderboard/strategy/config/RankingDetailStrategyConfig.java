package com.example.lunaproject.leaderboard.strategy.config;

import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.strategy.detail.RankingDetailStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class RankingDetailStrategyConfig {
    @Bean
    public Map<GameType, RankingDetailStrategy> strategyMap(
            List<RankingDetailStrategy> strategies
    ) {
        return strategies.stream()
                .collect(Collectors.toMap(
                        RankingDetailStrategy::getGameType,
                        Function.identity(),
                        (existing, replacement) -> { throw new IllegalStateException(); },
                        () -> new EnumMap<>(GameType.class)
                ));
    }
}