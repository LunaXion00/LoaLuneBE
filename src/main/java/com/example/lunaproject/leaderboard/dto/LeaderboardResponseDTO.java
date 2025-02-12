package com.example.lunaproject.leaderboard.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaderboardResponseDTO {
    private String streamerName;
    private int rank;
    private int rankChange;
    private Double itemLevel;
    private String streamerImageUrl;
}
