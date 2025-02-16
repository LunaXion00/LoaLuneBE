package com.example.lunaproject.leaderboard.entity;

import com.example.lunaproject.game.character.entity.LoaCharacter;
import com.example.lunaproject.game.character.repository.LoaCharacterRepository;
import com.example.lunaproject.streamer.entity.Streamer;
import com.example.lunaproject.global.utils.GameType;
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
    private int rankChange;
    private int rank;

    @ManyToOne
    @JoinColumn(name = "streamer_id", nullable = false)
    private Streamer streamer;

    @Column(columnDefinition = "TEXT")
    private String rankingDetails;

    @Column(columnDefinition = "TEXT")
    private String previousRankingDetails;

    @ManyToOne
    @JoinColumn(name = "character_id", nullable = false)
    private LoaCharacter character;

}