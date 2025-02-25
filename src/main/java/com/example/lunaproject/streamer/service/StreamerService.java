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
import com.example.lunaproject.streamer.entity.GameProfile;
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
        Streamer streamer = streamerRepository.findByChannelId(channelId)
                .orElseGet(() -> {
                    try {
                        return createNewStreamer(channelId);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
        if (hasExistingGameProfile(streamer, gameType)) {
            throw new IllegalArgumentException("해당 스트리머의 " + gameType + " 프로필이 이미 존재합니다");
        }
        GameProfile gameProfile = createGameProfile(streamer, gameType, mainCharacter);
        Streamer savedStreamer = streamerRepository.save(streamer);
        return buildResponseDTO(savedStreamer);
    }

    public StreamerWithCharacterDTO getStreamerInfo(String streamerName, GameType gameType){
        Streamer streamer = streamerRepository.findByStreamerName(streamerName)
                .orElseThrow(() -> new IllegalArgumentException("스트리머를 찾을 수 없습니다: " + streamerName));
        GameProfile gameProfile = streamer.getGameProfiles().stream()
                .filter(p -> p.getGameType() == gameType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 스트리머의 "+gameType+ "기록이 존재하지 않습니다."));

        String mainCharacterName = gameProfile.getCharacters().stream()
                .filter(c -> c.getId().equals(gameProfile.getMainCharacterId()))
                .findFirst()
                .map(GameCharacter::getCharacterName)
                .orElse(null);

        List<GameCharacterDTO> characterDTOs = gameProfile.getCharacters().stream()
                .map(this::convertToDTO)
                .toList();

        Set<TagDTO> tags = streamer.getTags().stream()
                .map(tag -> new TagDTO(tag.getTagName()))
                .collect(Collectors.toSet());

        return StreamerWithCharacterDTO.builder()
                .streamerName(streamer.getStreamerName())
                .mainCharacter(mainCharacterName)
                .channelId(streamer.getChannelId())
                .channelImageUrl(streamer.getChannelImageUrl())
                .characters(characterDTOs)
                .tags(tags)
                .build();
    }
    private <D extends GameCharacterDTO, T extends GameCharacter> List<T> createCharacterList(GameType gameType, String characterName, GameProfile gameProfile){
        GameApiClient<D> apiClient = apiClientRegistry.getClient(gameType);
        List<D> dtos = apiClient.createCharacterList(characterName);
        CharacterFactory<T, D> factory = characterFactoryRegistry.getFactory(gameType);
        return dtos.stream()
                .map(dto -> {
                    T character = factory.createCharacter(dto);
                    character.setGameProfile(gameProfile);
                    return character;
                })
                .collect(Collectors.toList());
    }

    private Streamer createNewStreamer(String channelId) throws JsonProcessingException {
        ChzzkResponseDTO chzzkResponseDTO =  new ObjectMapper().readValue(chzzkStreamerApiClient.findStreamerByChannelId(channelId).toString(), ChzzkResponseDTO.class);
        ChzzkResponseDTO.Content content = chzzkResponseDTO.getContent();
        return Streamer.builder()
                .streamerName(content.getChannelName())
                .channelId(channelId)
                .channelImageUrl(content.getChannelImageUrl())
                .build();
    }
    private boolean hasExistingGameProfile(Streamer streamer, GameType gameType) {
        return streamer.getGameProfiles().stream()
                .anyMatch(p -> p.getGameType() == gameType);
    }
    private GameProfile createGameProfile(Streamer streamer, GameType gameType, String mainCharacter) {
        // 게임 프로필 생성
        GameProfile gameProfile = GameProfile.builder()
                .gameType(gameType)
                .streamer(streamer)
                .characters(new ArrayList<>())
                .build();

        // 캐릭터 생성 및 연결
        List<GameCharacter> characters = createCharacterList(gameType, mainCharacter, gameProfile);
        setMainCharacter(gameProfile, characters, mainCharacter);

        // 양방향 관계 설정
        gameProfile.getCharacters().addAll(characters);
        streamer.getGameProfiles().add(gameProfile);

        return gameProfile;
    }

    private void setMainCharacter(GameProfile profile, List<GameCharacter> characters, String mainChar) {
        characters.stream()
                .filter(c -> c.getCharacterName().equals(mainChar))
                .findFirst()
                .ifPresentOrElse(
                        c -> {
                            logger.info(c.getId().toString());
                            profile.setMainCharacterId(c.getId());
                        },
                        () -> { throw new IllegalArgumentException("메인 캐릭터 생성 실패"); }
                );
    }
    private StreamerResponseDTO buildResponseDTO(Streamer streamer) {
        return StreamerResponseDTO.builder()
                .streamerName(streamer.getStreamerName())
                .build();
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

    private GameCharacterDTO convertToDTO(GameCharacter character) {
        if (character instanceof LoaCharacter loaChar) {
            return new LoaCharacterDTO(loaChar);
        }
        throw new IllegalArgumentException("Unsupported character type: "
                + character.getClass().getSimpleName());
    }
}
