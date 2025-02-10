package com.example.lunaproject.streamer.entity;

import com.example.lunaproject.global.utils.GameType;
import jakarta.persistence.*;

@Entity
public class StreamerGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="streamer_id", nullable = false)
    private Streamer streamer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameType gameType;
}
