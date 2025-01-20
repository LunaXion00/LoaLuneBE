package com.example.lunaproject.character.service;

import com.example.lunaproject.character.dto.CharacterDTO;
import com.example.lunaproject.character.entity.LoaCharacter;
import com.example.lunaproject.character.repository.CharactersRepository;
import com.example.lunaproject.lostark.LostarkCharacterApiClient;
import com.example.lunaproject.streamer.entity.Streamer;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CharacterService {
    private static final Logger logger = LoggerFactory.getLogger(CharacterService.class);

    private final CharactersRepository repository;
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
    public LoaCharacter addCharacterToRepository(CharacterDTO dto){
        LoaCharacter character = LoaCharacter.builder()
                .serverName(dto.getServerName())
                .characterName(dto.getCharacterName())
                .characterClassName(dto.getCharacterClassName())
                .characterLevel(dto.getCharacterLevel())
                .itemLevel(dto.getItemLevel())
                .streamer(Streamer.builder().streamerName("포셔").build())
                .characterImage(dto.getCharacterImage())
                .build();
        return repository.save(character);
    }


    @Transactional(readOnly = true)
    public LoaCharacter get(long id, String characterName){
        return repository.findByCharacterName(characterName).orElseThrow(
                () -> new IllegalArgumentException("characterName = " + characterName + " : 존재하지 않는 캐릭터"));
    }

    @Transactional
    public LoaCharacter registerCharacter(String characterName, Streamer streamer){
        Optional<LoaCharacter> exist = repository.findByCharacterName(characterName);
        if(exist.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 캐릭터: "+ characterName);
        }
//        LoaCharacter characterInfo = apiClient.findCharacterDetailsByApi(characterName, apiKey);
        return null;
    }
}
