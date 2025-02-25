package com.example.lunaproject.game.character.entity;

import com.example.lunaproject.game.character.dto.LoaCharacterDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Table(name= "loa_character")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DiscriminatorValue("lostark")
@Entity
public class LoaCharacter extends GameCharacter<LoaCharacterDTO> {
    @Column(name = "server")
    private String serverName;

    @Column(name = "character_level")
    private int characterLevel;

    @Column(name = "class")
    private String characterClassName;

    @Column(name = "item_level")
    private Double itemLevel;

    @Column(name = "character_image")
    private String characterImage;

    @Override
    public void updateCharacter(LoaCharacterDTO dto){
        setCharacterName(dto.getCharacterName());
        this.characterLevel = dto.getCharacterLevel();
        this.itemLevel = dto.getItemLevel();
        this.characterImage = dto.getCharacterImage();
    }
}
