package com.example.lunaproject.leaderboard.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseLeaderboardResDTO {
    // 스트리머 정보
    private String streamerName;
    private String channelId;
    private String streamerImageUrl;
    // 랭킹 정보
    private int rank;
    private int rankChange;
    // 랭킹 요소(게임에 따라 레벨이거나 클리어 시간 또는 티어 등을 JSON으로 제공.)
    private String rankingDetails;

    //신규 등록 스트리머 정보 식별
    private boolean isNewStreamer;
}
