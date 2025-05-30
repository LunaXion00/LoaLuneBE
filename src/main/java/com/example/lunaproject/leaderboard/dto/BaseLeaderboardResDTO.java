package com.example.lunaproject.leaderboard.dto;

import com.example.lunaproject.streamer.dto.TagDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

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

    //필터링을 위한 태그 정보 추가
    private Set<String> tags;
}
