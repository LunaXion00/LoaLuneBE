package com.example.lunaproject.leaderboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class VlrtLeaderboardResDTO extends BaseLeaderboardResDTO{
    // 발로란트에 필요한 캐릭터명, 클래스, 레벨 변동 정보.
    private String characterName;
    private String gameServer;
    private Double rrChange;
}
