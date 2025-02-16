package com.example.lunaproject.leaderboard.service;

import com.example.lunaproject.leaderboard.dto.LeaderboardResponseDTO;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.entity.Leaderboard;
import com.example.lunaproject.leaderboard.repository.LeaderboardRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static com.example.lunaproject.global.utils.GlobalMethods.extractItemLevel;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    private final LeaderboardRepository leaderboardRepository;
    public List<LeaderboardResponseDTO> getLeaderboard(GameType gameType){
        List<Leaderboard> leaderboardList = leaderboardRepository.findByGameTypeOrderByRankAsc(gameType);

        return leaderboardList.stream().map(entry -> {
            return LeaderboardResponseDTO.builder()
                    .streamerName(entry.getStreamer().getStreamerName())
                    .channelId(entry.getStreamer().getChannelId())
                    .streamerImageUrl(entry.getStreamer().getChannelImageUrl())
                    .rank(entry.getRank())
                    .rankChange(entry.getRankChange())
                    .characterName(entry.getCharacter().getCharacterName())
                    .characterClassName(entry.getCharacter().getCharacterClassName())
                    .rankingDetails(entry.getRankingDetails())
                    .itemLevelChange(extractItemLevel(entry.getRankingDetails())-extractItemLevel(entry.getPreviousRankingDetails()))
                    .isNewStreamer(entry.getPreviousRankingDetails()==null)
                    .build();
        }).collect(Collectors.toList());
    }
}
