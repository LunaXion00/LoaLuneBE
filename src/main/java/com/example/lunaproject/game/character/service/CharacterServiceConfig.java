package com.example.lunaproject.game.character.service;

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
public class CharacterServiceConfig {
    @Bean
    public Map<GameType, CharacterService> serviceMap(
            List<CharacterService> services
    ) {
        return services.stream()
                .collect(Collectors.toMap(
                        CharacterService::getGameType,
                        Function.identity(),
                        (existing, replacement) -> { throw new IllegalStateException(); },
                        () -> new EnumMap<>(GameType.class)
                ));
    }
}
