package com.example.lunaproject.leaderboard.entity;

import com.example.lunaproject.game.character.entity.LoaCharacter;
import com.example.lunaproject.game.character.repository.LoaCharacterRepository;
import com.example.lunaproject.streamer.entity.GameProfile;
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

    private int rankChange;
    private int rank;

    @ManyToOne
    @JoinColumn(name = "game_profile_id", nullable = false)
    private GameProfile gameProfile;

    @Column(columnDefinition = "TEXT")
    private String rankingDetails;

    @Column(columnDefinition = "TEXT")
    private String previousRankingDetails;
}