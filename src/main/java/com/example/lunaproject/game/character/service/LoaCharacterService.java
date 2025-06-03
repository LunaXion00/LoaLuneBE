package com.example.lunaproject.game.character.service;

import com.example.lunaproject.api.admin.dto.ModifyCharacterDTO;
import com.example.lunaproject.api.client.GameApiClient;
import com.example.lunaproject.game.character.Factory.CharacterFactory;
import com.example.lunaproject.game.character.Factory.CharacterFactoryRegistry;
import com.example.lunaproject.game.character.Factory.LoaCharacterFactory;
import com.example.lunaproject.game.character.dto.GameCharacterDTO;
import com.example.lunaproject.game.character.dto.LoaCharacterDTO;
import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.game.character.entity.LoaCharacter;
import com.example.lunaproject.game.character.repository.LoaCharacterRepository;
import com.example.lunaproject.api.lostark.client.LostarkCharacterApiClient;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.streamer.dto.StreamerRequestDTO;
import com.example.lunaproject.streamer.entity.GameProfile;
import com.example.lunaproject.streamer.entity.Streamer;
import com.example.lunaproject.streamer.repository.StreamerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.lunaproject.global.utils.GlobalMethods.isSameUUID;

@Service
@RequiredArgsConstructor
public class LoaCharacterService implements CharacterService{

    private final LoaCharacterRepository loaCharacterRepository;
    private final StreamerRepository streamerRepository;
    private final LostarkCharacterApiClient apiClient;
    private final CharacterFactoryRegistry characterFactoryRegistry;

    // ------------------------------- Create Method -------------------------------
    @Override
    public List<GameCharacter> addCharacters(StreamerRequestDTO requestDTO, GameProfile profile) {
        List<LoaCharacterDTO> dtos = apiClient.createCharacterList(requestDTO);
        CharacterFactory characterFactory = characterFactoryRegistry.getFactory(GameType.lostark);
        List<LoaCharacterDTO> filteredDtos = dtos.stream()
                .filter(dto->!loaCharacterRepository.existsByCharacterName(dto.getCharacterName()))
                .collect(Collectors.toList());;
        return filteredDtos.stream()
                .map(dto -> {
                    GameCharacter character = characterFactory.createCharacter(dto);
                    loaCharacterRepository.save((LoaCharacter) character);
                    character.setGameProfile(profile);
                    return character;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public LoaCharacter addCharacterToGameProfile(LoaCharacterDTO dto, GameProfile gameProfile){
        LoaCharacter character = LoaCharacter.builder()
                .serverName(dto.getServerName())
                .characterName(dto.getCharacterName())
                .characterClassName(dto.getCharacterClassName())
                .characterLevel(dto.getCharacterLevel())
                .itemLevel(dto.getItemLevel())
                .gameProfile(gameProfile)
                .characterImage(dto.getCharacterImage())
                .build();
        gameProfile.getCharacters().add(character);
        return loaCharacterRepository.save(character);
    }
    // ------------------------------- Retrieve Method -------------------------------


    // ------------------------------- Update Method -------------------------------
    @Transactional
    @Override
    public void updateCharacters(String channelId){
        Streamer streamer = streamerRepository.findByChannelId(channelId).orElseThrow();

        GameProfile loaProfile = streamer.getGameProfiles().stream()
                .filter(p -> p.getGameType() == GameType.lostark)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 스트리머의 로스트아크 기록이 존재하지 않습니다."));

        List<LoaCharacterDTO> siblings = apiClient.getSiblings(getMainCharacter(loaProfile).getCharacterName());

        siblings.stream()
                .map(dto -> {
                    LoaCharacterDTO updated = apiClient.getCharacter(dto.getCharacterName());
                    return (updated != null && updated.getCharacterImage() != null) ? updated : dto;
                })
                .forEach(dto -> updateCharacter(dto, loaProfile));
    }

    private void updateCharacter(LoaCharacterDTO dto, GameProfile loaProfile){
        List<LoaCharacter> findCharacterListUUID = loaProfile.getCharacters().stream()
                .filter(character -> character instanceof LoaCharacter) // 타입 체크
                .map(character -> (LoaCharacter) character) // 안전한 캐스팅
                .filter(loaCharacter -> loaCharacter.getCharacterImage() != null)
                .filter(loaCharacter -> isSameUUID(loaCharacter.getCharacterImage(), dto.getCharacterImage()))
                .toList();
        if (!findCharacterListUUID.isEmpty()){
            for(LoaCharacter character: findCharacterListUUID){
                if(character.getCharacterName().equals(getMainCharacter(loaProfile).getCharacterName())){
                    loaProfile.setMainCharacter(character);
                }
                character.updateCharacter(dto);
            }
        }
        else {
            // UUID가 일치하지 않으면 이름으로 캐릭터 찾기
            List<LoaCharacter> findCharacterListName = loaProfile.getCharacters().stream()
                    .filter(character -> character instanceof LoaCharacter) // 타입 체크
                    .map(character -> (LoaCharacter) character)
                    .filter(character -> character.getCharacterName().equals(dto.getCharacterName()))
                    .toList();

            if (!findCharacterListName.isEmpty()) {
                // 이름으로 찾은 캐릭터가 있으면 업데이트
                for (LoaCharacter character : findCharacterListName) {
                    character.updateCharacter(dto);
                }
            }
            else{
                LoaCharacter character = addCharacterToGameProfile(dto, loaProfile);
                loaProfile.addCharacter(character);
            }
        }
    }

    @Override
    public void modifyCharacterInfo(String channelId, ModifyCharacterDTO gameCharacterDTO) {

    }

    // ------------------------------- Private Method -------------------------------
    @Override
    public GameType getGameType() {
        return GameType.lostark;
    }

    @Override
    public GameCharacter determineMainCharacter(List<GameCharacter> characters) {
        return characters.stream()
                .map(c -> (LoaCharacter) c)
                .max(Comparator.comparingDouble(LoaCharacter::getItemLevel))
                .orElseThrow();
    }

    public GameCharacter getMainCharacter(GameProfile profile) {
        return profile.getCharacters().stream()
                .filter(c -> c.equals(profile.getMainCharacter()))
                .findFirst()
                .orElseThrow();
    }
}
