package com.example.lunaproject.game.character.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class GameCharacterDTO {
    @JsonProperty("CharacterName")
    private String characterName;
}
