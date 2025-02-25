package com.example.lunaproject.api.client;

import com.example.lunaproject.game.character.dto.GameCharacterDTO;
import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.global.utils.GameType;

import java.util.List;

public interface GameApiClient<T extends GameCharacterDTO> {
    List<T> createCharacterList(String characterName);
    GameType getGameType();
}
