package com.example.lunaproject.game.character.service;

import com.example.lunaproject.game.character.dto.LoaCharacterDTO;
import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.game.character.entity.LoaCharacter;
import com.example.lunaproject.game.character.repository.LoaCharacterRepository;
import com.example.lunaproject.api.lostark.client.LostarkCharacterApiClient;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.streamer.entity.GameProfile;
import com.example.lunaproject.streamer.entity.Streamer;
import com.example.lunaproject.streamer.repository.StreamerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.example.lunaproject.global.utils.GlobalMethods.isSameUUID;

@Service
@RequiredArgsConstructor
public class LoaCharacterService implements CharacterService{
    private static final Logger logger = LoggerFactory.getLogger(LoaCharacterService.class);

    private final LoaCharacterRepository loaCharacterRepository;
    private final StreamerRepository streamerRepository;
    private final LostarkCharacterApiClient apiClient;
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
    @Transactional
    @Override
    public void updateCharacters(String streamerName){
        Streamer streamer = streamerRepository.get(streamerName);

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

    @Override
    public GameType getGameType() {
        return GameType.lostark;
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
    public GameCharacter getMainCharacter(GameProfile profile) {
        return profile.getCharacters().stream()
                .filter(c -> c.equals(profile.getMainCharacter()))
                .findFirst()
                .orElseThrow();
    }
}
