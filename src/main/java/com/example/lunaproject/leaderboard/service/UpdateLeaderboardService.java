package com.example.lunaproject.leaderboard.service;

import com.example.lunaproject.game.character.repository.LoaCharacterRepository;
import com.example.lunaproject.game.enums.GameType;
import com.example.lunaproject.leaderboard.entity.Leaderboard;
import com.example.lunaproject.leaderboard.repository.LeaderboardRepository;
import com.example.lunaproject.streamer.repository.StreamerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateLeaderboardService {
    private final LeaderboardRepository leaderboardRepository;
    private final LoaCharacterRepository loaCharacterRepository;
    private final StreamerRepository streamerRepository;

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
//        log.info("{} 리더보드 업데이트 중...", gameType);
//
//        leaderboardRepository.deleteByGameType(gameType.name());
//
//        List<Leaderboard> newRankings = createNewLeaderboard(gameType);
//
//        assignRanking(newRankings);
//
//        leaderboardRepository.saveAll(newRankings);
//
//        log.info("{} 리더보드 갱신 완료!", gameType);
    }
}
