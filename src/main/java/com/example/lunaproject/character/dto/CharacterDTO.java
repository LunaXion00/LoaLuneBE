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
    private String itemLevel;
    public CharacterDTO(final LoaCharacter character){
        this.id = character.getId();
        this.serverName = character.getServerName();
        this.characterName = character.getCharacterName();
        this.characterClassName = character.getCharacterClassName();
        this.itemLevel = character.getItemLevel();
        this.characterLevel = character.getCharacterLevel();
    }
    @Override
    public String toString() {
        return "CharacterInfoDTO{" +
                "serverName='" + serverName + '\'' +
                ", characterName='" + characterName + '\'' +
                ", characterLevel='" + characterLevel + '\'' +
                ", characterClassName='" + characterClassName + '\'' +
                ", itemLevel='" + itemLevel + '\'' +
                '}';
    }
    public static LoaCharacter toEntity(CharacterDTO dto){
        return LoaCharacter.builder()
                .id((int) dto.getId())
                .serverName(dto.getServerName())
                .characterName(dto.getCharacterName())
                .characterClassName(dto.getCharacterClassName())
                .characterLevel(dto.getCharacterLevel())
                .itemLevel(dto.getItemLevel())
                .build();
    }
}
