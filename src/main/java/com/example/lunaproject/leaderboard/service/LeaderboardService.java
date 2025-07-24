package com.example.lunaproject.leaderboard.service;

import com.example.lunaproject.leaderboard.dto.BaseLeaderboardResDTO;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.entity.Leaderboard;
import com.example.lunaproject.leaderboard.repository.LeaderboardRepository;
import com.example.lunaproject.leaderboard.strategy.conversion.ConversionStrategyRegistry;
import com.example.lunaproject.leaderboard.strategy.conversion.LeaderboardConversionStrategy;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    private final LeaderboardRepository leaderboardRepository;
    private final ConversionStrategyRegistry conversionStrategyRegistry;
    private static final Logger logger = LoggerFactory.getLogger(LeaderboardService.class);

    @Cacheable(value = "leaderboard", key="#gameType.name()", cacheManager = "redisCacheManager")
    public List<? extends BaseLeaderboardResDTO> getLeaderboard(GameType gameType){
        List<Leaderboard> leaderboardList = leaderboardRepository.findByGameType(gameType);
        LeaderboardConversionStrategy conversionStrategy = conversionStrategyRegistry.getStrategy(gameType);
        logger.info("캐시 미적용: "+ gameType);
        return leaderboardList.stream()
                .map(entry-> {
                    BaseLeaderboardResDTO dto;
                    try {
                        dto = conversionStrategy.convert(entry);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
