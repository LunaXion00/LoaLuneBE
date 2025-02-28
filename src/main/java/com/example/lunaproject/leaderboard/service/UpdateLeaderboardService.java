package com.example.lunaproject.leaderboard.service;

import com.example.lunaproject.game.character.entity.LoaCharacter;
import com.example.lunaproject.game.character.repository.LoaCharacterRepository;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.entity.Leaderboard;
import com.example.lunaproject.leaderboard.repository.LeaderboardRepository;
import com.example.lunaproject.streamer.entity.GameProfile;
import com.example.lunaproject.streamer.entity.Streamer;
import com.example.lunaproject.streamer.repository.GameProfileRepository;
import com.example.lunaproject.streamer.repository.StreamerRepository;
import com.example.lunaproject.streamer.service.StreamerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.lunaproject.global.utils.GlobalMethods.extractItemLevel;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateLeaderboardService {
    private final LeaderboardRepository leaderboardRepository;
    private final LoaCharacterRepository loaCharacterRepository;
    private final GameProfileRepository gameProfileRepository;
    private final StreamerRepository streamerRepository;
    private final StreamerService streamerService;
    @Scheduled(cron = "00 43 * * * *")
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

        List<Streamer> streamers = streamerRepository.findAll();
        for(Streamer streamer:streamers) streamerService.updateStreamerCharacters(streamer.getStreamerName());

        List<Leaderboard> newRankings = createNewLeaderboard(gameType);
        leaderboardRepository.saveAll(newRankings);
        newRankings = leaderboardRepository.findAll();

        assignRanking(newRankings, oldRanking);

        leaderboardRepository.saveAll(newRankings);
    }
    private List<Leaderboard> createNewLeaderboard(GameType gameType){
        List<GameProfile> gameProfiles = gameProfileRepository.findAllByGameType(gameType);
        return gameProfiles.stream()
                .map(profile->{
                    LoaCharacter mainCharacter = loaCharacterRepository.findById(profile.getMainCharacter().getId()).orElse(null);
                    return Leaderboard.builder()
                            .gameProfile(profile)
                            .rank(0)
                            .rankChange(0)
                            .rankingDetails("{\"itemLevel\": " + mainCharacter.getItemLevel() + "}")
                            .previousRankingDetails(null) // 이전 데이터는 별도 처리
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    private void assignRanking(List<Leaderboard> leaderboards, List<Leaderboard> oldRanking) {
        leaderboards.sort((a, b) -> Double.compare(
                extractItemLevel(b.getRankingDetails()), extractItemLevel(a.getRankingDetails())

        ));
        int rank = 1;
        for (Leaderboard entry : leaderboards) {
            entry.setRank(rank++);
            entry.setRankChange(calculateRankChange(entry, oldRanking));

            Double previousItemLevel = extractItemLevel(getPreviousRankingDetails(entry, oldRanking));
            if (previousItemLevel == null) {
                entry.setPreviousRankingDetails(null); // 새 스트리머의 경우 현재 값 저장
            } else {
                entry.setPreviousRankingDetails(getPreviousRankingDetails(entry, oldRanking));
            }
        }
    }
    private int calculateRankChange(Leaderboard newEntry, List<Leaderboard> oldRanking) {
        return oldRanking.stream()
                .filter(oldEntry -> oldEntry.getGameProfile().getStreamer().equals(newEntry.getGameProfile().getStreamer())
                        && oldEntry.getGameProfile().getGameType().equals(newEntry.getGameProfile().getGameType()))
                .findFirst()
                .map(oldEntry -> oldEntry.getRank() - newEntry.getRank()) // 이전 순위 - 새 순위
                .orElse(0);
    }

    private String getPreviousRankingDetails(Leaderboard newEntry, List<Leaderboard> oldRanking) {
        return oldRanking.stream()
                .filter(oldEntry -> oldEntry.getGameProfile().getStreamer().equals(newEntry.getGameProfile().getStreamer())
                        && oldEntry.getGameProfile().getGameType().equals(newEntry.getGameProfile().getGameType()))
                .findFirst()
                .map(Leaderboard::getRankingDetails)
                .orElse(null); // 이전 값이 없으면 null 반환
    }
}
