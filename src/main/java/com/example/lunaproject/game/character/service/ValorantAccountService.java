package com.example.lunaproject.game.character.service;

import com.example.lunaproject.api.admin.dto.ModifyCharacterDTO;
import com.example.lunaproject.api.valorant.client.ValorantAccountApiClient;
import com.example.lunaproject.game.character.Factory.CharacterFactory;
import com.example.lunaproject.game.character.Factory.CharacterFactoryRegistry;
import com.example.lunaproject.game.character.dto.LoaCharacterDTO;
import com.example.lunaproject.game.character.dto.VlrtAccountDTO;
import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.game.character.entity.VlrtAccount;
import com.example.lunaproject.game.character.repository.VlrtAccountRepository;
import com.example.lunaproject.game.character.utils.VlrtTier;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.streamer.dto.StreamerRequestDTO;
import com.example.lunaproject.streamer.entity.GameProfile;
import com.example.lunaproject.streamer.entity.Streamer;
import com.example.lunaproject.streamer.repository.StreamerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValorantAccountService implements CharacterService{
    private final ValorantAccountApiClient apiClient;
    private final VlrtAccountRepository vlrtAccountRepository;
    private final StreamerRepository streamerRepository;
    private final CharacterFactoryRegistry characterFactoryRegistry;
    private static final Logger logger = LoggerFactory.getLogger(ValorantAccountService.class);

    // ------------------------------- Create Method -------------------------------
    @Override
    public List<GameCharacter> addCharacters(StreamerRequestDTO requestDTO, GameProfile profile) {
        List<VlrtAccountDTO> dtos = apiClient.createCharacterList(requestDTO);
        CharacterFactory characterFactory = characterFactoryRegistry.getFactory(GameType.vlrt);
        List<VlrtAccountDTO> filteredDtos = dtos.stream()
                .filter(dto->!vlrtAccountRepository.existsByCharacterName(dto.getCharacterName()))
                .collect(Collectors.toList());;
        return filteredDtos.stream()
                .map(dto -> {
                    GameCharacter character = characterFactory.createCharacter(dto);
                    vlrtAccountRepository.save((VlrtAccount) character);
                    character.setGameProfile(profile);
                    return character;
                })
                .collect(Collectors.toList());
    }
    // ------------------------------- Retrieve Method -------------------------------
    public List<VlrtAccountDTO> getAccountInfo(StreamerRequestDTO requestDTO){
        List<VlrtAccountDTO> dtos = apiClient.createCharacterList(requestDTO);
        return dtos;
    }
    // ------------------------------- Update Method -------------------------------
    @Transactional
    @Override
    public void updateCharacters(String channelId) {
        Streamer streamer = streamerRepository.findByChannelId(channelId).orElseThrow();

        GameProfile vlrtProfile = streamer.getGameProfiles().stream()
                .filter(p -> p.getGameType() == GameType.vlrt)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 스트리머의 발로란트 기록이 존재하지 않습니다."));
        VlrtAccount vlrtAccount = (VlrtAccount) vlrtProfile.getMainCharacter();
        String[] parts = vlrtAccount.getCharacterName().split("#");

        try {
            String updatedTier = apiClient.getRankByApi(parts[0], parts[1], vlrtAccount.getServer());
            logger.info("streamerName: "+updatedTier);
            if(updatedTier == null) return;
            String[] responseParts = updatedTier.split(" - ");
            String tierString = responseParts[0].replace(" ", "_").toUpperCase();
            int rr = Integer.parseInt(responseParts[1].replace("RR.", ""));
            VlrtAccountDTO dto = VlrtAccountDTO.builder()
                    .tier(VlrtTier.fromApiString(tierString))
                    .rr(rr)
                    .characterName(vlrtAccount.getCharacterName())
                    .server(vlrtAccount.getServer())
                    .build();
            vlrtAccount.updateCharacter(dto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void modifyCharacterInfo(String channelId, ModifyCharacterDTO modifyCharacterDTO) {
        VlrtAccount target = vlrtAccountRepository.findByCharacterName(modifyCharacterDTO.getBeforeCharacterName());
        target.setCharacterName(modifyCharacterDTO.getAfterCharacterName());
        vlrtAccountRepository.save(target);
    }
    // ------------------------------- Delete Method -------------------------------

    // ------------------------------- Private Method -------------------------------
    @Override
    public GameType getGameType() {
        return GameType.vlrt;
    }

    @Override
    public GameCharacter determineMainCharacter(List<GameCharacter> characters) {
        return characters.stream()
                .map(c -> (VlrtAccount) c)
                .max(Comparator.comparingInt(a ->
                        a.getTier().getRankValue() * 1000 + a.getRr()))
                .orElseThrow();
    }

}
