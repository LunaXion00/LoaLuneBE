package com.example.lunaproject.leaderboard.strategy;

import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.game.character.entity.LoaCharacter;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.dto.BaseLeaderboardResDTO;
import com.example.lunaproject.leaderboard.dto.LoaLeaderboardResDTO;
import com.example.lunaproject.leaderboard.entity.Leaderboard;
import com.example.lunaproject.streamer.entity.Streamer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.example.lunaproject.global.utils.GlobalMethods.extractItemLevel;

@Component
@RequiredArgsConstructor
public class LoaConversionStrategy implements LeaderboardConversionStrategy{
    private final ObjectMapper objectMapper;
    @Override
    public BaseLeaderboardResDTO convert(Leaderboard leaderboard) {
        GameCharacter mainCharacter = leaderboard.getGameProfile().getMainCharacter();
        Streamer streamer = leaderboard.getGameProfile().getStreamer();
        return LoaLeaderboardResDTO.builder()
                .streamerName(streamer.getStreamerName())
                .streamerImageUrl(streamer.getChannelImageUrl())
                .channelId(streamer.getChannelId())
                .rank(leaderboard.getRank())
                .rankChange(leaderboard.getRankChange())
                .rankingDetails(leaderboard.getRankingDetails())
                .characterName(mainCharacter.getCharacterName())
                .characterClassName(((LoaCharacter)mainCharacter).getCharacterClassName())
                .itemLevelChange(extractItemLevel(leaderboard.getRankingDetails())-extractItemLevel(leaderboard.getPreviousRankingDetails()))
                .isNewStreamer(leaderboard.getPreviousRankingDetails()==null)
                .build();
    }

    @Override
    public GameType getGameType() {
        return GameType.lostark;
    }
}
