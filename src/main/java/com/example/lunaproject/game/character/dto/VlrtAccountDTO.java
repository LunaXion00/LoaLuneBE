package com.example.lunaproject.game.character.dto;

import com.example.lunaproject.game.character.entity.LoaCharacter;
import com.example.lunaproject.game.character.entity.VlrtAccount;
import com.example.lunaproject.game.character.utils.VlrtTier;
import com.example.lunaproject.global.utils.GameServer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VlrtAccountDTO extends GameCharacterDTO{
    @JsonProperty("Tier")
    private VlrtTier tier;

    @JsonProperty("RankRating")
    private int rr;

    @JsonProperty("server")
    private GameServer server;

    public VlrtAccountDTO(final VlrtAccount account){
        setCharacterName(account.getCharacterName());
        this.tier = account.getTier();
        this.rr = account.getRr();
        this.server = account.getServer();
    }
}
