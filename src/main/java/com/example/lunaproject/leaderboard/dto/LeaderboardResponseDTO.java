package com.example.lunaproject.leaderboard.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaderboardResponseDTO {
    // 스트리머 관련 정보
    private String streamerName;
    private String channelId;
    private String streamerImageUrl;
    // 랭킹 정보
    private int rank;
    private int rankChange;
    //캐릭터 정보
    private String characterName;
    private String characterClassName;
    private String rankingDetails;
}
