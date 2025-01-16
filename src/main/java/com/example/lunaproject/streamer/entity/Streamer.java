package com.example.lunaproject.streamer.entity;

import com.example.lunaproject.character.entity.LoaCharacter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "streamer")
@Builder
@Entity
public class Streamer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "streamer_id")
    private Long id;

    @Column(name="streamer_name")
    private String streamerName;

    @Column(name="main_character")
    private String mainCharacter;

    @Column(name="channel_id")
    private String channelId;

    @OneToMany(mappedBy = "streamer", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference
    private List<LoaCharacter> characters;

    public void addCharacter(LoaCharacter character){
        characters.add(character);
        character.setStreamer(this);
    }
    public void editMainCharacter(String mainCharacter){
        this.mainCharacter = mainCharacter;
    }
    public void createCharacter(List<LoaCharacter> characterList, String mainCharacter){
        characterList.stream()
                .peek(character->character.setStreamer(this))
                .forEach(characters::add);
        this.mainCharacter = mainCharacter;
    }
}
