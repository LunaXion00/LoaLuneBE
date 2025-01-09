package com.example.lunaproject.character.dto;


import com.example.lunaproject.character.entity.LoaCharacter;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CharacterDTO {
    private long id;
    private String serverName;
    private String characterName;
    private String characterClassName;
    private int characterLevel;
    private double itemLevel;
    public CharacterDTO(final LoaCharacter character){
        this.id = character.getId();
        this.serverName = character.getServerName();
        this.characterName = character.getCharacterName();
        this.characterClassName = character.getCharacterClassName();
        this.itemLevel = character.getItemLevel();
    }
    @Override
    public String toString() {
        return "CharacterInfoDTO{" +
                "serverName='" + serverName + '\'' +
                ", characterName='" + characterName + '\'' +
                ", characterLevel=" + characterLevel +
                ", characterClassName='" + characterClassName + '\'' +
                ", itemAvgLevel='" + itemLevel + '\'' +
                '}';
    }
}
