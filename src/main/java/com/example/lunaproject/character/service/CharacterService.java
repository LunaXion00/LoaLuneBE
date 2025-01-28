package com.example.lunaproject.character.service;

import com.example.lunaproject.character.dto.CharacterDTO;
import com.example.lunaproject.character.entity.LoaCharacter;
import com.example.lunaproject.character.repository.CharactersRepository;
import com.example.lunaproject.lostark.LostarkCharacterApiClient;
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
public class CharacterService {
    private static final Logger logger = LoggerFactory.getLogger(CharacterService.class);

    private final CharactersRepository charactersRepository;
    private final StreamerRepository streamerRepository;
    private final LostarkCharacterApiClient apiClient;
    @Value("${Lostark-API-KEY}")
    String apiKey;

    public JSONArray Characters(String characterName){
        try{
            JSONArray array = apiClient.findCharactersByApi(characterName, apiKey);
            logger.info("----"+array.toString());
            return array;
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    public JSONObject characterDetails(String characterName){
        try {
            JSONObject object = apiClient.findCharacterDetailsByApi(characterName, apiKey);
            logger.info("===="+object.toString());
            return object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Transactional
    public LoaCharacter addCharacterToRepository(CharacterDTO dto, Streamer streamer){
        LoaCharacter character = LoaCharacter.builder()
                .serverName(dto.getServerName())
                .characterName(dto.getCharacterName())
                .characterClassName(dto.getCharacterClassName())
                .characterLevel(dto.getCharacterLevel())
                .itemLevel(dto.getItemLevel())
                .streamer(streamer)
                .characterImage(dto.getCharacterImage())
                .build();
        return charactersRepository.save(character);
    }


    @Transactional(readOnly = true)
    public LoaCharacter get(long id, String characterName){
        return charactersRepository.findByCharacterName(characterName).orElseThrow(
                () -> new IllegalArgumentException("characterName = " + characterName + " : 존재하지 않는 캐릭터"));
    }

    @Transactional
    public LoaCharacter registerCharacter(String characterName, Streamer streamer){
        Optional<LoaCharacter> exist = charactersRepository.findByCharacterName(characterName);
        if(exist.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 캐릭터: "+ characterName);
        }
//        LoaCharacter characterInfo = apiClient.findCharacterDetailsByApi(characterName, apiKey);
        return null;
    }

    public void updateSibling(String streamerName){
        Streamer streamer = streamerRepository.get(streamerName);

        String mainCharacter = streamer.getMainCharacter();

        List<CharacterDTO> siblings = apiClient.getSiblings(mainCharacter, apiKey);
        siblings.stream()
                .map(dto-> {
                    CharacterDTO updatedCharacter = apiClient.getCharacter(dto.getCharacterName(), apiKey);
                    if(updatedCharacter != null && updatedCharacter.getCharacterImage() != null) dto = updatedCharacter;
                    return dto;
                })
                .forEach(dto-> updateCharacter(dto, streamer, mainCharacter));
    }
    private void updateCharacter(CharacterDTO dto, Streamer streamer, String mainCharacter){
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
