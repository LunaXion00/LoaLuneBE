package com.example.lunaproject.streamer.service;

import com.example.lunaproject.character.dto.CharacterDTO;
import com.example.lunaproject.character.entity.LoaCharacter;
import com.example.lunaproject.character.service.CharacterService;
import com.example.lunaproject.lostark.LostarkCharacterApiClient;
import com.example.lunaproject.streamer.dto.*;
import com.example.lunaproject.streamer.entity.Streamer;
import com.example.lunaproject.streamer.repository.StreamerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StreamerService {
    private final StreamerRepository streamerRepository;
    private final LostarkCharacterApiClient lostarkCharacterApiClient;
    private final ChzzkStreamerApiClient chzzkStreamerApiClient;
    private final CharacterService characterService;
    private static final Logger logger = LoggerFactory.getLogger(CharacterService.class);

    @Value("${Lostark-API-KEY}")
    String apiKey;

    public StreamerDTO createStreamer(StreamerRequestDTO requestDTO) throws JsonProcessingException {
        String mainCharacter = requestDTO.getMainCharacter();
        String channelId = requestDTO.getChannelId();

        if(existStreamer(channelId)) throw new IllegalArgumentException("해당 스트리머는 이미 등록되어있습니다.");

        ChzzkResponseDTO chzzkResponseDTO =  new ObjectMapper().readValue(chzzkStreamerApiClient.findStreamerByChannelId(channelId).toString(), ChzzkResponseDTO.class);
        ChzzkResponseDTO.Content content = chzzkResponseDTO.getContent();

        StreamerDTO dto = new StreamerDTO()
                .builder()
                .streamerName(content.getChannelName())
                .channelImageUrl(content.getChannelImageUrl())
                .build();

        List<LoaCharacter> characterList = createStreamerCharacterList(mainCharacter);
        Streamer streamer = Streamer.builder()
                .streamerName(dto.getStreamerName())
                .mainCharacter(mainCharacter)
                .channelId(requestDTO.getChannelId())
                .characters(new ArrayList<>())
                .channelImageUrl(dto.getChannelImageUrl())
                .build();

        streamer.createCharacter(characterList);
        streamerRepository.save(streamer);
        return dto;
    }

    @Transactional(readOnly = true)
    public Streamer get(String streamerName){
        return streamerRepository.get(streamerName);
    }

    @Transactional
    public void editMainCharacter(String streamerName, String mainCharacter){
        Streamer streamer = get(streamerName);

    }

    public StreamerWithCharacterDTO getStreamerInfo(String streamerName){
        Streamer streamer = streamerRepository.findByStreamerName(streamerName)
                .orElseThrow(() -> new IllegalArgumentException("스트리머를 찾을 수 없습니다: " + streamerName));

        List<CharacterDTO> characterDTOS = streamer.getCharacters().stream()
                .map(CharacterDTO::new)
                .collect(Collectors.toList());

        Set<TagDTO> tags = streamer.getTags().stream()
                .map(tag -> new TagDTO(tag.getName()))
                .collect(Collectors.toSet());

        return StreamerWithCharacterDTO.builder()
                .streamerName(streamerName)
                .mainCharacter(streamer.getMainCharacter())
                .channelId(streamer.getChannelId())
                .channelImageUrl(streamer.getChannelImageUrl())
                .characters(characterDTOS)
                .tags(tags)
                .build();

    }
    public boolean existStreamer(String channelId){
        return streamerRepository.existsByChannelId(channelId);
    }

    private List<LoaCharacter> createStreamerCharacterList(String characterName){
        List<LoaCharacter> characterList = lostarkCharacterApiClient.createCharacterList(characterName, apiKey);
        return characterList.stream().collect(Collectors.toList());
    }

    @Transactional
    public void updateStreamerCharacters(String streamerName){
        Streamer streamer = streamerRepository.findByStreamerName(streamerName)
                .orElseThrow(() -> new IllegalArgumentException("스트리머를 찾을 수 없습니다: " + streamerName));
        characterService.updateSibling(streamerName);
    }
}
