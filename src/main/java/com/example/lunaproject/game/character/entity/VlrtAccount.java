package com.example.lunaproject.game.character.entity;

import com.example.lunaproject.game.character.dto.GameCharacterDTO;
import com.example.lunaproject.game.character.dto.VlrtAccountDTO;
import com.example.lunaproject.game.character.utils.VlrtTier;
import com.example.lunaproject.global.utils.GameServer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Table(name= "vlrt_account")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DiscriminatorValue("vlrt")
@Entity
public class VlrtAccount extends GameCharacter<VlrtAccountDTO> {
    @Enumerated(EnumType.STRING)
    @Column(name = "tier")
    private VlrtTier tier;

    @Column(name = "rr")
    private int rr;

    @Enumerated(EnumType.STRING)
    @Column(name = "server")
    private GameServer server;
    @Override
    public void updateCharacter(VlrtAccountDTO dto) {

    }
}
