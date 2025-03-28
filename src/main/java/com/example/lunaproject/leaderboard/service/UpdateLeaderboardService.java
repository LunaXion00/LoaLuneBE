package com.example.lunaproject.leaderboard.service;

import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.entity.Leaderboard;
import com.example.lunaproject.leaderboard.repository.LeaderboardRepository;
import com.example.lunaproject.leaderboard.strategy.detail.RankingDetailStrategy;
import com.example.lunaproject.streamer.entity.Streamer;
import com.example.lunaproject.streamer.repository.GameProfileRepository;
import com.example.lunaproject.streamer.repository.StreamerRepository;
import com.example.lunaproject.streamer.service.StreamerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateLeaderboardService {
    private final LeaderboardRepository leaderboardRepository;
    private final GameProfileRepository gameProfileRepository;
    private final Map<GameType, RankingDetailStrategy> rankingStrategies;
    private final StreamerRepository streamerRepository;
    private final StreamerService streamerService;
    private static final Logger logger = LoggerFactory.getLogger(UpdateLeaderboardService.class);
    @Scheduled(cron = "00 00 09 * * *")
    public void updateAllLeaderboards(){
        log.info("모든 게임의 리더보드 업데이트 시작...");
        for(GameType type:GameType.values()){
            try{
                updateLeaderboard(type);
                log.info("{} 리더보드 업데이트 완료", type);
            } catch(Exception e){
                log.error("{} 리더보드 업데이트 중 오류 발생 {}", type, e.getMessage());
            }
        }
    }
    public void updateLeaderboard(GameType gameType) {
        List<Leaderboard> oldRanking = leaderboardRepository.findByGameType(gameType);

        leaderboardRepository.deleteByGameProfile_GameType(gameType);

        List<Streamer> targetStreamers = streamerRepository.findByGameProfiles_GameType(gameType);

        targetStreamers.forEach(streamer ->
                streamerService.updateStreamerCharacters(streamer.getStreamerName(), gameType)
        );

        List<Leaderboard> newRankings = createNewLeaderboard(gameType);
        leaderboardRepository.saveAll(newRankings);
        newRankings = leaderboardRepository.findByGameType(gameType);

        assignRanking(newRankings, oldRanking, gameType);

        leaderboardRepository.saveAll(newRankings);
    }
    private List<Leaderboard> createNewLeaderboard(GameType gameType){
        return gameProfileRepository.findAllByGameType(gameType).stream()
                .map(profile->{
                    GameCharacter mainCharacter = profile.getMainCharacter();
                    RankingDetailStrategy strategy = rankingStrategies.get(gameType);
                    return Leaderboard.builder()
                            .gameProfile(profile)
                            .rank(0)
                            .rankChange(0)
                            .rankingDetails(strategy.generateDetails(mainCharacter))
                            .previousRankingDetails(null) // 이전 데이터는 별도 처리
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    private void assignRanking(List<Leaderboard> leaderboards, List<Leaderboard> oldRanking, GameType gameType) {
        RankingDetailStrategy strategy = rankingStrategies.get(gameType);
        leaderboards.sort((a, b) -> {
            int mainCompare = Double.compare(strategy.calculateRankValue(b.getRankingDetails()), strategy.calculateRankValue(a.getRankingDetails()));
            if(mainCompare != 0) return mainCompare;
            LocalDate aDate = strategy.getRefreshDate(a.getRankingDetails());
            LocalDate bDate = strategy.getRefreshDate(b.getRankingDetails());
            return aDate.compareTo(bDate);
        });
        int currentRank = 1;
        int actualPosition = 1;
        Double prevValue = null;
        LocalDate prevTime = null;
        for (Leaderboard entry : leaderboards) {
            double currentValue = strategy.calculateRankValue(entry.getRankingDetails());
            LocalDate currentTime =  strategy.getRefreshDate(entry.getRankingDetails());
            if(prevValue!= null && currentValue==prevValue && currentTime.equals(prevTime)){
                entry.setRank(currentRank);
            } else{
                currentRank = actualPosition;
                entry.setRank(currentRank);
            }
            actualPosition++;
            prevValue = currentValue;
            prevTime = currentTime;
            entry.setRankChange(calculateRankChange(entry, oldRanking));
            String oldRankingDetails = getOldRankingDetails(entry, oldRanking);
            if (oldRankingDetails == null) {
                logger.info("There is no oldRankingDetails");
                entry.setPreviousRankingDetails(null);
            } else {
                if(strategy.calculateRankValue(entry.getRankingDetails()) == strategy.calculateRankValue(oldRankingDetails)) entry.setRankingDetails(oldRankingDetails);
                entry.setPreviousRankingDetails(oldRankingDetails);
            }
        }
    }
    private int calculateRankChange(Leaderboard newEntry, List<Leaderboard> oldRanking) {
        return oldRanking.stream()
                .filter(oldEntry ->oldEntry.getGameProfile().getId().equals(newEntry.getGameProfile().getId()))
                .findFirst()
                .map(oldEntry -> oldEntry.getRank() - newEntry.getRank()) // 이전 순위 - 새 순위
                .orElse(0);
    }

    private String getOldRankingDetails(Leaderboard newEntry, List<Leaderboard> oldRanking) {
        return oldRanking.stream()
                .filter(oldEntry ->oldEntry.getGameProfile().getId().equals(newEntry.getGameProfile().getId()))
                .findFirst()
                .map(Leaderboard::getRankingDetails)
                .orElse(null); // 이전 값이 없으면 null 반환
    }
}
