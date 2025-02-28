package com.example.lunaproject.leaderboard.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class LoaLeaderboardResDTO extends BaseLeaderboardResDTO {
    // 로스트아크에 필요한 캐릭터명, 클래스, 레벨 변동 정보.
    private String characterName;
    private String characterClassName;
    private double itemLevelChange;
}
