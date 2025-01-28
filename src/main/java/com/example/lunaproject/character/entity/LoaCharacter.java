package com.example.lunaproject.character.entity;

import com.example.lunaproject.character.dto.CharacterDTO;
import com.example.lunaproject.streamer.entity.Streamer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Table(name= "loa_character")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class LoaCharacter{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "character_id")
    private Long id;

    @Column(name = "server")
    private String serverName;

    @Column(name = "character_name")
    private String characterName;

    @Column(name = "character_level")
    private int characterLevel;

    @Column(name = "class")
    private String characterClassName;

    @Column(name = "item_level")
    private Double itemLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "streamer_id")
    @JsonBackReference
    private Streamer streamer;

    @Column(name = "character_image")
    private String characterImage;

    public void updateLoaCharacter(CharacterDTO dto){
        this.characterName = dto.getCharacterName();
        this.characterLevel = dto.getCharacterLevel();
        this.itemLevel = dto.getItemLevel();
        this.characterImage = dto.getCharacterImage();
    }
}
