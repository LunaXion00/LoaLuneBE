package com.example.lunaproject.game.character.entity;

import com.example.lunaproject.game.character.dto.GameCharacterDTO;
import com.example.lunaproject.game.character.dto.LoaCharacterDTO;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.streamer.entity.Streamer;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "game_type")
public abstract class GameCharacter<T extends GameCharacterDTO> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String characterName;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_type", nullable = false, insertable = false, updatable = false)
    private GameType gameType;

    @ManyToOne
    @JoinColumn(name ="streamer_id")
    private Streamer streamer;

    public abstract void updateCharacter(T dto);
}
