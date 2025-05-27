package com.example.lunaproject.game.character.service;

import com.example.lunaproject.api.admin.dto.ModifyCharacterDTO;
import com.example.lunaproject.game.character.dto.GameCharacterDTO;
import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.streamer.dto.StreamerRequestDTO;
import com.example.lunaproject.streamer.entity.GameProfile;

import java.util.List;

public interface CharacterService {
    void updateCharacters(String streamerName);
    GameType getGameType();
    GameCharacter determineMainCharacter(List<GameCharacter> characters);
    List<GameCharacter> addCharacters(StreamerRequestDTO dto, GameProfile profile);
    void modifyCharacterInfo(String channelId, ModifyCharacterDTO gameCharacterDTO);
}
