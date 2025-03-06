package com.example.lunaproject.game.character.service;

import com.example.lunaproject.api.valorant.client.ValorantAccountApiClient;
import com.example.lunaproject.game.character.dto.VlrtAccountDTO;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class ValorantAccountService implements CharacterService{
    private final ValorantAccountApiClient apiClient;
    private final VlrtAccountRepository vlrtAccountRepository;
    private final StreamerRepository streamerRepository;
    private static final Logger logger = LoggerFactory.getLogger(ValorantAccountService.class);

    public List<VlrtAccountDTO> getAccountInfo(StreamerRequestDTO requestDTO){
        List<VlrtAccountDTO> dtos = apiClient.createCharacterList(requestDTO);
        return dtos;
    }

    @Transactional
    @Override
    public void updateCharacters(String streamerName) {
        Streamer streamer = streamerRepository.get(streamerName);

        GameProfile vlrtProfile = streamer.getGameProfiles().stream()
                .filter(p -> p.getGameType() == GameType.vlrt)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 스트리머의 발로란트 기록이 존재하지 않습니다."));
        VlrtAccount vlrtAccount = (VlrtAccount) vlrtProfile.getMainCharacter();
        String[] parts = vlrtAccount.getCharacterName().split("#");

        try {
            String updatedTier = apiClient.getRankByApi(parts[0], parts[1], vlrtAccount.getServer());
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
    public GameType getGameType() {
        return GameType.vlrt;
    }
}
