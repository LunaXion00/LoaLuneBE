package com.example.lunaproject.leaderboard.service;

import com.example.lunaproject.leaderboard.dto.BaseLeaderboardResDTO;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.entity.Leaderboard;
import com.example.lunaproject.leaderboard.repository.LeaderboardRepository;
import com.example.lunaproject.leaderboard.strategy.ConversionStrategyRegistry;
import com.example.lunaproject.leaderboard.strategy.LeaderboardConversionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    private final LeaderboardRepository leaderboardRepository;
    private final ConversionStrategyRegistry conversionStrategyRegistry;

    public List<? extends BaseLeaderboardResDTO> getLeaderboard(GameType gameType){
        List<Leaderboard> leaderboardList = leaderboardRepository.findByGameType(gameType);
        LeaderboardConversionStrategy conversionStrategy = conversionStrategyRegistry.getStrategy(gameType);

        return leaderboardList.stream()
                .map(entry-> {
                    BaseLeaderboardResDTO dto = conversionStrategy.convert(entry);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
