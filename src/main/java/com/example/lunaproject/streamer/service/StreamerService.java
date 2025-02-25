package com.example.lunaproject.streamer.service;

import com.example.lunaproject.api.chzzk.client.ChzzkStreamerApiClient;
import com.example.lunaproject.api.chzzk.dto.ChzzkResponseDTO;
import com.example.lunaproject.api.client.GameApiClient;
import com.example.lunaproject.api.registry.GameApiClientRegistry;
import com.example.lunaproject.game.character.Factory.CharacterFactory;
import com.example.lunaproject.game.character.Factory.CharacterFactoryRegistry;
import com.example.lunaproject.game.character.dto.GameCharacterDTO;
import com.example.lunaproject.game.character.dto.LoaCharacterDTO;
import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.game.character.entity.LoaCharacter;
import com.example.lunaproject.game.character.service.LoaCharacterService;
import com.example.lunaproject.api.lostark.client.LostarkCharacterApiClient;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.streamer.dto.*;
import com.example.lunaproject.streamer.entity.Streamer;
import com.example.lunaproject.streamer.entity.Tag;
import com.example.lunaproject.streamer.repository.StreamerRepository;
import com.example.lunaproject.streamer.repository.TagRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final ChzzkStreamerApiClient chzzkStreamerApiClient;
    private final LoaCharacterService loaCharacterService;
    private final TagRepository tagRepository;
    private final GameApiClientRegistry apiClientRegistry;
    private final CharacterFactoryRegistry characterFactoryRegistry;
    private static final Logger logger = LoggerFactory.getLogger(LoaCharacterService.class);

    public StreamerResponseDTO createStreamer(StreamerRequestDTO requestDTO) throws JsonProcessingException {
        String mainCharacter = requestDTO.getMainCharacter();
        String channelId = requestDTO.getChannelId();
        GameType gameType = requestDTO.getGameType();
        if(existStreamer(channelId)) throw new IllegalArgumentException("해당 스트리머는 이미 등록되어있습니다.");

        ChzzkResponseDTO chzzkResponseDTO =  new ObjectMapper().readValue(chzzkStreamerApiClient.findStreamerByChannelId(channelId).toString(), ChzzkResponseDTO.class);
        ChzzkResponseDTO.Content content = chzzkResponseDTO.getContent();

        StreamerResponseDTO dto = new StreamerResponseDTO()
                .builder()
                .streamerName(content.getChannelName())
                .channelImageUrl(content.getChannelImageUrl())
                .build();

        Streamer streamer = Streamer.builder()
                .streamerName(dto.getStreamerName())
                .mainCharacter(mainCharacter)
                .channelId(requestDTO.getChannelId())
                .characters(new ArrayList<>())
                .channelImageUrl(dto.getChannelImageUrl())
                .build();
        List<GameCharacter> characterList = createCharacterList(gameType, mainCharacter, streamer);

        streamer.createCharacter(characterList);
        streamerRepository.save(streamer);
        return dto;
    }

    public StreamerWithCharacterDTO getStreamerInfo(String streamerName, GameType gameType){
        Streamer streamer = streamerRepository.findByStreamerName(streamerName)
                .orElseThrow(() -> new IllegalArgumentException("스트리머를 찾을 수 없습니다: " + streamerName));
        List<? extends GameCharacterDTO> characterDTOs = mapCharactersByGameType(
                streamer.getCharacters(),
                gameType
        );
        Set<TagDTO> tags = streamer.getTags().stream()
                .map(tag -> new TagDTO(tag.getTagName()))
                .collect(Collectors.toSet());

        return StreamerWithCharacterDTO.builder()
                .streamerName(streamerName)
                .mainCharacter(streamer.getMainCharacter())
                .channelId(streamer.getChannelId())
                .channelImageUrl(streamer.getChannelImageUrl())
                .characters(characterDTOs)
                .tags(tags)
                .build();
    }
    public boolean existStreamer(String channelId){
        return streamerRepository.existsByChannelId(channelId);
    }

    private <D extends GameCharacterDTO, T extends GameCharacter>
    List<T> createCharacterList(GameType gameType, String characterName, Streamer streamer){
        logger.info("gameType: "+gameType);
        GameApiClient<D> apiClient = apiClientRegistry.getClient(gameType);
        List<D> dtos = apiClient.createCharacterList(characterName);
        CharacterFactory<T, D> factory = characterFactoryRegistry.getFactory(gameType);
        return dtos.stream()
                .map(dto-> factory.createCharacter(dto, streamer))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStreamerCharacters(String streamerName){
        Streamer streamer = streamerRepository.findByStreamerName(streamerName)
                .orElseThrow(() -> new IllegalArgumentException("스트리머를 찾을 수 없습니다: " + streamerName));
        loaCharacterService.updateSibling(streamerName);
    }

    @Transactional
    public void updateStreamerTags(String channelId, List<String> tags){
        Streamer streamer = streamerRepository.findByChannelId(channelId).orElseThrow(()-> new IllegalArgumentException("스트리머를 찾을 수 없습니다"));
        Set<Tag> tagSet = tags.stream()
                .map(tagName -> tagRepository.findByTagName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setTagName(tagName);
                            return tagRepository.save(newTag);
                        }))
                .collect(Collectors.toSet());
        streamer.setTags(tagSet);
        streamerRepository.save(streamer);
    }

    private List<? extends GameCharacterDTO> mapCharactersByGameType(
            List<GameCharacter> characters,
            GameType gameType
    ) {
        return characters.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    private GameCharacterDTO convertToDTO(GameCharacter character) {
        return switch (character.getGameType()) {
            case lostark -> new LoaCharacterDTO((LoaCharacter) character);
        };
    }
}
