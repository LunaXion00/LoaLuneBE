package com.example.lunaproject.game.character.Factory;

import com.example.lunaproject.game.character.dto.LoaCharacterDTO;
import com.example.lunaproject.game.character.entity.LoaCharacter;
import com.example.lunaproject.game.character.repository.LoaCharacterRepository;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.streamer.entity.Streamer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoaCharacterFactory implements CharacterFactory<LoaCharacter, LoaCharacterDTO> {
    private final LoaCharacterRepository loaCharacterRepository;
    @Override
    public LoaCharacter createCharacter(LoaCharacterDTO dto) {
        LoaCharacter character = LoaCharacter.builder()
                .characterName(dto.getCharacterName())
                .serverName(dto.getServerName())
                .characterLevel(dto.getCharacterLevel())
                .characterClassName(dto.getCharacterClassName())
                .itemLevel(dto.getItemLevel())
                .characterImage(dto.getCharacterImage())
                .build();
        return loaCharacterRepository.save(character);
    }

    @Override
    public GameType getGameType() {
        return GameType.lostark;
    }
}