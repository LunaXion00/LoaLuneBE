package com.example.lunaproject.api.client;

import com.example.lunaproject.game.character.dto.GameCharacterDTO;
import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.streamer.dto.StreamerRequestDTO;

import java.util.List;

public interface GameApiClient<T extends GameCharacterDTO> {
    List<T> createCharacterList(StreamerRequestDTO requestDTO);
    GameType getGameType();
}
