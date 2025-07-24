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
public class UpdateLeaderboardService {
    private final LeaderboardUpdaterService leaderboardUpdaterService;
    private static final Logger logger = LoggerFactory.getLogger(UpdateLeaderboardService.class);
    @Scheduled(cron = "00 00 15 * * *")
    public void updateAllLeaderboards(){
        log.info("모든 게임의 리더보드 업데이트 시작...");
        for(GameType type:GameType.values()){
            try{
                leaderboardUpdaterService.updateLeaderboard(type);
                log.info("{} 리더보드 업데이트 완료", type);
            } catch(Exception e){
                log.error("{} 리더보드 업데이트 중 오류 발생 {}", type, e.getMessage());
            }
        }
    }
}
