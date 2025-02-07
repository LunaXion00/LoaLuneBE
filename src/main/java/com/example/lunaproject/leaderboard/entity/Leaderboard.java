package com.example.lunaproject.leaderboard.entity;

import com.example.lunaproject.game.enums.GameType;
import com.example.lunaproject.streamer.entity.Streamer;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
public class Leaderboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_type", nullable = false)
    private GameType gameType;
    private String streamerName;
    private int rankChange;
    private int rank;

    @ManyToOne
    @JoinColumn(name = "streamer_id")
    private Streamer streamer;

    @Column(columnDefinition = "TEXT")
    private String rankingDetails;
}
