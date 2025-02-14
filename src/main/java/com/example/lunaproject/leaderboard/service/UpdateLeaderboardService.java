package com.example.lunaproject.leaderboard.service;

import com.example.lunaproject.game.character.repository.LoaCharacterRepository;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.entity.Leaderboard;
import com.example.lunaproject.leaderboard.repository.LeaderboardRepository;
import com.example.lunaproject.streamer.entity.Streamer;
import com.example.lunaproject.streamer.repository.StreamerRepository;
import com.example.lunaproject.streamer.service.StreamerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateLeaderboardService {
    private final LeaderboardRepository leaderboardRepository;
    private final LoaCharacterRepository loaCharacterRepository;
    private final StreamerRepository streamerRepository;
    private final StreamerService streamerService;
    @Scheduled(cron = "00 5 16 * * *")
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
        leaderboardRepository.deleteByGameType(gameType);
        List<Streamer> streamers = streamerRepository.findAll();
        for(Streamer streamer:streamers) streamerService.updateStreamerCharacters(streamer.getStreamerName());
        List<Leaderboard> newRankings = createNewLeaderboard(gameType);
        leaderboardRepository.saveAll(newRankings);
        newRankings = leaderboardRepository.findAll();
        assignRanking(newRankings);
        leaderboardRepository.saveAll(newRankings);
    }
    private List<Leaderboard> createNewLeaderboard(GameType gameType){
        return streamerRepository.findAll().stream()
                .map(streamer -> loaCharacterRepository.findTopByStreamerOrderByItemLevelDesc(streamer)
                        .map(character -> Leaderboard.builder()
                                .gameType(gameType)
                                .rank(0) // 나중에 정렬 후 순위 지정
                                .rankChange(0)
                                .rankingDetails("{\"itemLevel\": " + character.getItemLevel() + "}")
                                .streamer(streamer)
                                .character(character)
                                .build())
                        .orElse(null))
                .filter(leaderboard -> leaderboard != null)
                .toList();
    }
    private void assignRanking(List<Leaderboard> leaderboards) {
        leaderboards.sort((a, b) -> Double.compare(
                Double.parseDouble(b.getRankingDetails().replace("{\"itemLevel\": ", "").replace("}", "")),
                Double.parseDouble(a.getRankingDetails().replace("{\"itemLevel\": ", "").replace("}", ""))

        ));

        int rank = 1;
        for (Leaderboard entry : leaderboards) {
            entry.setRank(rank++);
        }
    }
}
