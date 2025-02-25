package com.example.lunaproject.streamer.entity;

import com.example.lunaproject.game.character.dto.GameCharacterDTO;
import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.global.utils.GameType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "game_profile")
@Builder
@Entity
public class GameProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="streamer_id", nullable = false)
    private Streamer streamer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameType gameType;

    @JoinColumn(name = "main_character_id")
    private Long mainCharacterId;

    @OneToMany(mappedBy="gameProfile", cascade = CascadeType.ALL)
    private List<GameCharacter> characters;

    public void addCharacter(GameCharacter character){
        character.setGameProfile(this);
        characters.add(character);
    }
}
