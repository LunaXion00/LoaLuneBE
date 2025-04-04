package com.example.lunaproject.streamer.service;

import com.example.lunaproject.api.chzzk.client.ChzzkStreamerApiClient;
import com.example.lunaproject.api.chzzk.dto.ChzzkResponseDTO;
import com.example.lunaproject.api.client.GameApiClient;
import com.example.lunaproject.api.registry.GameApiClientRegistry;
import com.example.lunaproject.game.character.Factory.CharacterFactory;
import com.example.lunaproject.game.character.Factory.CharacterFactoryRegistry;
import com.example.lunaproject.game.character.dto.GameCharacterDTO;
import com.example.lunaproject.game.character.dto.LoaCharacterDTO;
import com.example.lunaproject.game.character.dto.VlrtAccountDTO;
import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.game.character.entity.LoaCharacter;
import com.example.lunaproject.game.character.entity.VlrtAccount;
import com.example.lunaproject.game.character.service.CharacterService;
import com.example.lunaproject.game.character.service.LoaCharacterService;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.service.UpdateLeaderboardService;
import com.example.lunaproject.streamer.dto.*;
import com.example.lunaproject.streamer.entity.GameProfile;
import com.example.lunaproject.streamer.entity.Streamer;
import com.example.lunaproject.streamer.entity.Tag;
import com.example.lunaproject.streamer.repository.GameProfileRepository;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StreamerService {
    private final StreamerRepository streamerRepository;
    private final ChzzkStreamerApiClient chzzkStreamerApiClient;
    private final Map<GameType, CharacterService> serviceMap;
    private final TagRepository tagRepository;
    private final GameProfileRepository gameProfileRepository;

    private static final Logger logger = LoggerFactory.getLogger(StreamerService.class);

    public StreamerResponseDTO createStreamer(StreamerRequestDTO requestDTO) {
        Streamer streamer = streamerRepository.findByChannelId(requestDTO.getChannelId())
                .orElseGet(() -> {
                    try {
                        return createNewStreamer(requestDTO.getChannelId());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
        GameProfile existingProfile = streamer.getGameProfiles().stream()
                .filter(p->p.getGameType() == requestDTO.getGameType())
                .findFirst()
                .orElse(null);
        if(existingProfile != null) {
            if(existingProfile.getCharacters().stream().anyMatch(c->c.getCharacterName().equals(requestDTO.getMainCharacter()))) throw new IllegalArgumentException("이미 등록된 캐릭터 또는 계정입니다.");
            logger.info("이미 존재하는 계정. 미중복 캐릭터만 추가.");
            addCharactersToProfile(existingProfile, requestDTO);
        } else{
            createGameProfile(streamer, requestDTO);
        }
        return buildResponseDTO(streamerRepository.save(streamer));
    }

    public StreamerWithCharacterDTO getStreamerInfo(String streamerName, GameType gameType){
        Streamer streamer = streamerRepository.findByStreamerName(streamerName)
                .orElseThrow(() -> new IllegalArgumentException("스트리머를 찾을 수 없습니다: " + streamerName));
        GameProfile gameProfile = streamer.getGameProfiles().stream()
                .filter(p -> p.getGameType() == gameType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 스트리머의 "+gameType+ "기록이 존재하지 않습니다."));

        String mainCharacterName = gameProfile.getCharacters().stream()
                .filter(c -> c.equals(gameProfile.getMainCharacter()))
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

    private Streamer createNewStreamer(String channelId) throws JsonProcessingException {
        ChzzkResponseDTO chzzkResponseDTO =  new ObjectMapper().readValue(chzzkStreamerApiClient.findStreamerByChannelId(channelId).toString(), ChzzkResponseDTO.class);
        ChzzkResponseDTO.Content content = chzzkResponseDTO.getContent();
        return Streamer.builder()
                .streamerName(content.getChannelName())
                .channelId(channelId)
                .channelImageUrl(content.getChannelImageUrl())
                .build();
    }
    private GameProfile createGameProfile(Streamer streamer, StreamerRequestDTO requestDTO) {
        // 게임 프로필 생성
        GameProfile gameProfile = GameProfile.builder()
                .gameType(requestDTO.getGameType())
                .streamer(streamer)
                .characters(new ArrayList<>())
                .build();

        // 캐릭터 생성 및 연결
        CharacterService service = serviceMap.get(requestDTO.getGameType());
        List<GameCharacter> characters = service.addCharacters(requestDTO, gameProfile);
        logger.info("createGameProfile로 생성.");
        setMainCharacter(gameProfile, characters, requestDTO.getMainCharacter());

        // 양방향 관계 설정
        gameProfile.getCharacters().addAll(characters);
        streamer.getGameProfiles().add(gameProfile);

        return gameProfile;
    }
    private void addCharactersToProfile(GameProfile profile, StreamerRequestDTO requestDTO){
        CharacterService characterService = serviceMap.get(requestDTO.getGameType());
        List<GameCharacter> characters = characterService.addCharacters(requestDTO, profile);

        List<GameCharacter> filtered = filterNewCharacters(profile, characters);
        filtered.forEach(newChar->{
            newChar.setGameProfile(profile);
            profile.getCharacters().add(newChar);
            logger.info("새로운 캐릭터: "+ requestDTO.getMainCharacter()+"가 추가되었습니다.");
        });
    }
    private List<GameCharacter> filterNewCharacters(GameProfile profile, List<GameCharacter> characters){
        return characters.stream().filter(character->!isCharacterExists(profile, character))
                .collect(Collectors.toList());
    }
    private boolean isCharacterExists(GameProfile gameProfile, GameCharacter gameCharacter){
        return gameProfile.getCharacters().stream().anyMatch(c->c.getCharacterName().equals(gameCharacter.getCharacterName()));
    }

    private boolean hasExistingGameProfile(Streamer streamer, GameType gameType) {
        return streamer.getGameProfiles().stream()
                .anyMatch(p -> p.getGameType() == gameType);
    }

    private void setMainCharacter(GameProfile profile, List<GameCharacter> characters, String mainChar) {
        characters.stream()
                .filter(c -> c.getCharacterName().equals(mainChar))
                .findFirst()
                .ifPresentOrElse(
                        c -> {
                            profile.setMainCharacter(c);
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
    public void updateStreamerCharacters(String streamerName, GameType gameType){
        Streamer streamer = streamerRepository.findByStreamerName(streamerName)
                .orElseThrow(() -> new IllegalArgumentException("스트리머를 찾을 수 없습니다: " + streamerName));
        CharacterService service = serviceMap.get(gameType);
        service.updateCharacters(streamerName);
        GameProfile profile = streamer.getGameProfiles().stream()
                .filter(p->p.getGameType()==gameType)
                .findFirst()
                .orElseThrow();

        GameCharacter newMain = service.determineMainCharacter(profile.getCharacters());
        profile.setMainCharacter(newMain);
        gameProfileRepository.save(profile);
        logger.info("메인 캐릭터 업데이트 완료: {} → {}",
                streamerName, newMain.getCharacterName());
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
        } else if(character instanceof VlrtAccount vlrtAccount){
            return new VlrtAccountDTO(vlrtAccount);
        }
        throw new IllegalArgumentException("Unsupported character type: "
                + character.getClass().getSimpleName());
    }
}
