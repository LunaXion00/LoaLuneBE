package com.example.lunaproject.game.character.Factory;

import com.example.lunaproject.game.character.dto.VlrtAccountDTO;
import com.example.lunaproject.game.character.entity.VlrtAccount;
import com.example.lunaproject.game.character.repository.VlrtAccountRepository;
import com.example.lunaproject.global.utils.GameType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VlrtAccountFactory implements CharacterFactory<VlrtAccount, VlrtAccountDTO> {
    private final VlrtAccountRepository vlrtAccountRepository;
    @Override
    public VlrtAccount createCharacter(VlrtAccountDTO dto) {
        return VlrtAccount.builder()
                .characterName(dto.getCharacterName())
                .server(dto.getServer())
                .tier(dto.getTier())
                .rr(dto.getRr())
                .build();
    }

    @Override
    public GameType getGameType() {
        return GameType.vlrt;
    }
}
