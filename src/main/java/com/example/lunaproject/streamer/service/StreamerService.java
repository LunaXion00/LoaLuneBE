package com.example.lunaproject.streamer.service;

import com.example.lunaproject.character.entity.LoaCharacter;
import com.example.lunaproject.character.service.CharacterService;
import com.example.lunaproject.lostark.LostarkCharacterApiClient;
import com.example.lunaproject.streamer.dto.StreamerRequestDTO;
import com.example.lunaproject.streamer.entity.Streamer;
import com.example.lunaproject.streamer.repository.StreamerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StreamerService {
    private final StreamerRepository streamerRepository;
    private final CharacterService characterService;
    private final LostarkCharacterApiClient lostarkCharacterApiClient;
    @Value("${Lostark-API-KEY}")
    String apiKey;

    public void createStreamer(StreamerRequestDTO requestDTO){
        String streamerName = requestDTO.getStreamerName();
        String mainCharacter = requestDTO.getMainCharacter();
        boolean exists = streamerRepository.existsByStreamerName(streamerName);
        if(exists) throw new IllegalArgumentException("해당 스트리머는 이미 등록되어있습니다.");
        List<LoaCharacter> characterList = createAndCalculateCharaters(mainCharacter);
        Streamer streamer = Streamer.builder()
                .streamerName(streamerName)
                .mainCharacter(mainCharacter)
                .characters(new ArrayList<>())
                .build();
        streamer.createCharacter(characterList, mainCharacter);
        streamerRepository.save(streamer);
//        characterService.registerCharacter(mainCharacter, streamer);
    }
    @Transactional(readOnly = true)
    public Streamer get(String streamerName){
        return streamerRepository.get(streamerName);
    }
    @Transactional
    public void editMainCharacter(String streamerName, String mainCharacter){
        Streamer streamer = get(streamerName);

    }
    private static void validateCreateStreamer(){

    }
    private List<LoaCharacter> createAndCalculateCharaters(String characterName){
        List<LoaCharacter> characterList = lostarkCharacterApiClient.createCharacterList(characterName, apiKey);
        return characterList.stream().collect(Collectors.toList());
    }
}
