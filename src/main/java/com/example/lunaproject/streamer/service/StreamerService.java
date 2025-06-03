package com.example.lunaproject.streamer.service;

import com.example.lunaproject.api.chzzk.client.ChzzkStreamerApiClient;
import com.example.lunaproject.api.chzzk.dto.ChzzkResponseDTO;
import com.example.lunaproject.game.character.dto.GameCharacterDTO;
import com.example.lunaproject.game.character.dto.LoaCharacterDTO;
import com.example.lunaproject.game.character.dto.VlrtAccountDTO;
import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.game.character.entity.LoaCharacter;
import com.example.lunaproject.game.character.entity.VlrtAccount;
import com.example.lunaproject.game.character.service.CharacterService;
import com.example.lunaproject.global.utils.GameType;
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

    // ------------------------------- Create Method -------------------------------
    public StreamerResponseDTO createStreamer(StreamerRequestDTO requestDTO) {
        Streamer streamer = streamerRepository.findByChannelId(requestDTO.getChannelId())
                .orElseGet(() -> {
                    try {
                        logger.info("신규 스트리머");
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
            addCharactersToProfile(existingProfile, requestDTO);
        } else{
            createGameProfile(streamer, requestDTO);
        }
        return buildStreamerResponseDTO(streamerRepository.save(streamer));
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
        GameProfile gameProfile = GameProfile.builder()
                .gameType(requestDTO.getGameType())
                .streamer(streamer)
                .characters(new ArrayList<>())
                .build();

        CharacterService service = serviceMap.get(requestDTO.getGameType());
        List<GameCharacter> characters = service.addCharacters(requestDTO, gameProfile);
        setMainCharacter(gameProfile, characters, requestDTO.getMainCharacter());

        gameProfile.getCharacters().addAll(characters);
        streamer.getGameProfiles().add(gameProfile);

        return gameProfile;
    }

    private void addCharactersToProfile(GameProfile profile, StreamerRequestDTO requestDTO){
        CharacterService characterService = serviceMap.get(requestDTO.getGameType());
        List<GameCharacter> characters = characterService.addCharacters(requestDTO, profile);

        logger.info(characters.toString());

        List<GameCharacter> filtered = filterNewCharacters(profile, characters);

        filtered.forEach(newChar->{
            newChar.setGameProfile(profile);
            profile.getCharacters().add(newChar);
            logger.info("새로운 캐릭터: "+ requestDTO.getMainCharacter()+"가 추가되었습니다.");
        });
    }

    // ------------------------------- Retrieve Method -------------------------------
    public StreamerWithCharacterDTO getStreamerInfo(String channelId, GameType gameType){
        Streamer streamer = validateStreamerExists(channelId);
        GameProfile gameProfile = validateGameProfileExists(streamer, gameType);

        String mainCharacterName = gameProfile.getCharacters().stream()
                .filter(c -> c.equals(gameProfile.getMainCharacter()))
                .findFirst()
                .map(GameCharacter::getCharacterName)
                .orElse(null);

        List<GameCharacterDTO> characterDTOs = gameProfile.getCharacters().stream()
                .map(this::convertToGameCharacterDTO)
                .toList();

        Set<TagDTO> tags = streamer.getTags().stream()
                .filter(tag -> tag.getGameType().equals(GameType.common) || tag.getGameType().equals(gameType))
                .map(tag -> new TagDTO(tag.getTagName(), tag.getGameType()))
                .collect(Collectors.toSet());

        return convertToStreamerWithCharacterDTO(streamer, mainCharacterName, characterDTOs, tags);
    }

    @Transactional(readOnly = true)
    public List<StreamerInfoDTO> getAllStreamerInfo() {
        List<Streamer> streamers = streamerRepository.findAll();

        return streamers.stream()
                .map(streamer -> StreamerInfoDTO.builder()
                        .streamerName(streamer.getStreamerName())
                        .channelId(streamer.getChannelId())
                        .existGame(streamer.getGameProfiles().stream().map(GameProfile::getGameType).distinct().collect(Collectors.toList()))
                        .tags(
                                streamer.getTags().stream()
                                        .map(tag -> new TagDTO(tag.getTagName(), tag.getGameType()))
                                        .collect(Collectors.toSet())
                        )
                        .build()
                ).toList();
    }

    // ------------------------------- Update Method -------------------------------
    @Transactional
    public void updateStreamerCharacters(String channelId, GameType gameType){
        Streamer streamer = validateStreamerExists(channelId);
        CharacterService service = serviceMap.get(gameType);
        service.updateCharacters(channelId);
        GameProfile profile = streamer.getGameProfiles().stream()
                .filter(p->p.getGameType()==gameType)
                .findFirst()
                .orElseThrow();

        GameCharacter newMain = service.determineMainCharacter(profile.getCharacters());
        profile.setMainCharacter(newMain);
        gameProfileRepository.save(profile);
        logger.info("메인 캐릭터 업데이트 완료: {} → {}",
                streamer.getStreamerName(), newMain.getCharacterName());
    }

    @Transactional
    public void updateStreamerTags(String channelId, List<TagDTO> tags){
        Streamer streamer = validateStreamerExists(channelId);
        Set<Tag> tagEntities = tags.stream()
                .map(tagDto -> tagRepository.findByTagNameAndGameType(tagDto.getTagName(), tagDto.getGameType())
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setTagName(tagDto.getTagName());
                            newTag.setGameType(tagDto.getGameType());
                            return tagRepository.save(newTag);
                        }))
                .collect(Collectors.toSet());
        streamer.setTags(tagEntities);
        streamerRepository.save(streamer);
    }

    // ------------------------------- Validate Method -------------------------------
    private boolean isCharacterExists(GameProfile gameProfile, GameCharacter gameCharacter){
        return gameProfile.getCharacters().stream().anyMatch(c->c.getCharacterName().equals(gameCharacter.getCharacterName()));
    }

    private Streamer validateStreamerExists(String channelId){
        return streamerRepository.findByChannelId(channelId)
                .orElseThrow(() -> new IllegalArgumentException("스트리머를 찾을 수 없습니다: " + channelId));
    }
    private GameProfile validateGameProfileExists(Streamer streamer, GameType gameType){
        return streamer.getGameProfiles().stream()
                .filter(p -> p.getGameType() == gameType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 스트리머의 "+gameType+ "기록이 존재하지 않습니다."));
    }
    // ------------------------------- Delete Method -------------------------------


    // ------------------------------- Private Method -------------------------------
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

    private List<GameCharacter> filterNewCharacters(GameProfile profile, List<GameCharacter> characters){
        return characters.stream().filter(character->!isCharacterExists(profile, character))
                .collect(Collectors.toList());
    }

    private StreamerResponseDTO buildStreamerResponseDTO(Streamer streamer) {
        return StreamerResponseDTO.builder()
                .streamerName(streamer.getStreamerName())
                .build();
    }

    private GameCharacterDTO convertToGameCharacterDTO(GameCharacter character) {
        if (character instanceof LoaCharacter loaChar) {
            return new LoaCharacterDTO(loaChar);
        } else if(character instanceof VlrtAccount vlrtAccount){
            return new VlrtAccountDTO(vlrtAccount);
        }
        throw new IllegalArgumentException("Unsupported character type: "
                + character.getClass().getSimpleName());
    }

    private StreamerWithCharacterDTO convertToStreamerWithCharacterDTO(Streamer streamer, String mainCharacterName, List<GameCharacterDTO> characterDTOs,Set<TagDTO> tags){
        return StreamerWithCharacterDTO.builder()
                .streamerName(streamer.getStreamerName())
                .mainCharacter(mainCharacterName)
                .channelId(streamer.getChannelId())
                .channelImageUrl(streamer.getChannelImageUrl())
                .characters(characterDTOs)
                .tags(tags)
                .build();
    }
}
