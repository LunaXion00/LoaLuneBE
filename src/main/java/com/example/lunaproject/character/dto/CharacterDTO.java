package com.example.lunaproject.character.dto;


import com.example.lunaproject.character.entity.LoaCharacter;
import com.example.lunaproject.global.utils.DoubleDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterDTO {
    private long id;
    @JsonProperty("ServerName")
    private String serverName;

    @JsonProperty("CharacterName")
    private String characterName;

    @JsonProperty("CharacterClassName")
    private String characterClassName;

    @JsonProperty("CharacterLevel")
    private int characterLevel;

    @JsonProperty("ItemMaxLevel")
    @JsonDeserialize(using = DoubleDeserializer.class)
    private Double itemLevel;

    @JsonProperty("CharacterImage")
    private String characterImage;

    public CharacterDTO(final LoaCharacter character){
        this.id = character.getId();
        this.serverName = character.getServerName();
        this.characterName = character.getCharacterName();
        this.characterClassName = character.getCharacterClassName();
        this.itemLevel = character.getItemLevel();
        this.characterLevel = character.getCharacterLevel();
        this.characterImage = character.getCharacterImage();
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
                .id((Long) dto.getId())
                .serverName(dto.getServerName())
                .characterName(dto.getCharacterName())
                .characterClassName(dto.getCharacterClassName())
                .characterLevel(dto.getCharacterLevel())
                .itemLevel(dto.getItemLevel())
                .build();
    }
}
