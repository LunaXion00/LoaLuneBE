package com.example.lunaproject.leaderboard.strategy.conversion;

import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.game.character.entity.VlrtAccount;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.dto.BaseLeaderboardResDTO;
import com.example.lunaproject.leaderboard.dto.VlrtLeaderboardResDTO;
import com.example.lunaproject.leaderboard.entity.Leaderboard;
import com.example.lunaproject.streamer.entity.Streamer;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import static com.example.lunaproject.leaderboard.utils.LeaderboardMethod.extractVlrtRr;


@Component
@RequiredArgsConstructor
public class VlrtConversionStrategy implements LeaderboardConversionStrategy {
    @Override
    public BaseLeaderboardResDTO convert(Leaderboard leaderboard) throws ParseException {
        GameCharacter mainCharacter = leaderboard.getGameProfile().getMainCharacter();
        Streamer streamer = leaderboard.getGameProfile().getStreamer();
        return VlrtLeaderboardResDTO.builder()
                .streamerName(streamer.getStreamerName())
                .streamerImageUrl(streamer.getChannelImageUrl())
                .channelId(streamer.getChannelId())
                .rank(leaderboard.getRank())
                .rankChange(leaderboard.getRankChange())
                .rankingDetails(leaderboard.getRankingDetails())
                .characterName(mainCharacter.getCharacterName())
                .gameServer(((VlrtAccount)mainCharacter).getServer().toString())
                .rrChange(extractVlrtRr(leaderboard.getRankingDetails())-extractVlrtRr(leaderboard.getPreviousRankingDetails()))
                .isNewStreamer(leaderboard.getPreviousRankingDetails()==null)
                .build();
    }

    @Override
    public GameType getGameType() {
        return GameType.vlrt;
    }
}
