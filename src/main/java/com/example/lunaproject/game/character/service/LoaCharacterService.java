package com.example.lunaproject.game.character.service;

import com.example.lunaproject.game.character.dto.LoaCharacterDTO;
import com.example.lunaproject.game.character.entity.LoaCharacter;
import com.example.lunaproject.game.character.repository.LoaCharacterRepository;
import com.example.lunaproject.api.lostark.client.LostarkCharacterApiClient;
import com.example.lunaproject.streamer.entity.Streamer;
import com.example.lunaproject.streamer.repository.StreamerRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.example.lunaproject.global.utils.GlobalMethods.isSameUUID;

@Service
@RequiredArgsConstructor
public class LoaCharacterService {
    private static final Logger logger = LoggerFactory.getLogger(LoaCharacterService.class);

    private final LoaCharacterRepository loaCharacterRepository;
    private final StreamerRepository streamerRepository;
    private final LostarkCharacterApiClient apiClient;
    @Value("${Lostark-API-KEY}")
    String apiKey;
    @Transactional
    public LoaCharacter addCharacterToRepository(LoaCharacterDTO dto, Streamer streamer){
        LoaCharacter character = LoaCharacter.builder()
                .serverName(dto.getServerName())
                .characterName(dto.getCharacterName())
                .characterClassName(dto.getCharacterClassName())
                .characterLevel(dto.getCharacterLevel())
                .itemLevel(dto.getItemLevel())
                .streamer(streamer)
                .characterImage(dto.getCharacterImage())
                .build();
        return loaCharacterRepository.save(character);
    }
    public void updateSibling(String streamerName){
        Streamer streamer = streamerRepository.get(streamerName);

        String mainCharacter = streamer.getMainCharacter();

        List<LoaCharacterDTO> siblings = apiClient.getSiblings(mainCharacter, apiKey);
        siblings.stream()
                .map(dto-> {
                    LoaCharacterDTO updatedCharacter = apiClient.getCharacter(dto.getCharacterName(), apiKey);
                    if(updatedCharacter != null && updatedCharacter.getCharacterImage() != null) dto = updatedCharacter;
                    return dto;
                })
                .forEach(dto-> updateCharacter(dto, streamer, mainCharacter));
    }
    private void updateCharacter(LoaCharacterDTO dto, Streamer streamer, String mainCharacter){
        List<LoaCharacter> findCharacterListUUID= streamer.getCharacters().stream()
                .filter(character-> character.getCharacterImage() != null)
                .filter(character -> isSameUUID(character.getCharacterImage(), dto.getCharacterImage()))
                .toList();
        if (!findCharacterListUUID.isEmpty()){
            for(LoaCharacter character: findCharacterListUUID){
                if(character.getCharacterName().equals(mainCharacter)){
                    streamer.setMainCharacter(mainCharacter);
                }
                character.updateLoaCharacter(dto);
            }
        }
        else {
            // UUID가 일치하지 않으면 이름으로 캐릭터 찾기
            List<LoaCharacter> findCharacterListName = streamer.getCharacters().stream()
                    .filter(character -> character.getCharacterName().equals(dto.getCharacterName()))
                    .toList();

            if (!findCharacterListName.isEmpty()) {
                // 이름으로 찾은 캐릭터가 있으면 업데이트
                for (LoaCharacter character : findCharacterListName) {
                    character.updateLoaCharacter(dto);
                }
            }
            else{
                LoaCharacter character = addCharacterToRepository(dto, streamer);
                streamer.addCharacter(character);
            }
        }
    }
}
