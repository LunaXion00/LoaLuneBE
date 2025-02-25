package com.example.lunaproject.game.character.Factory;

import com.example.lunaproject.game.character.dto.GameCharacterDTO;
import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.streamer.entity.Streamer;

public interface CharacterFactory<T extends GameCharacter, D extends GameCharacterDTO> {
    T createCharacter(D dto);
    GameType getGameType();
}
