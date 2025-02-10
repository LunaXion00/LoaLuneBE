package com.example.lunaproject.game.character.entity;

import com.example.lunaproject.streamer.entity.Streamer;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "game_type")
public abstract class GameCharacter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String characterName;

    @ManyToOne
    @JoinColumn(name ="streamer_id")
    private Streamer streamer;
}
