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
    private int id;

    @Column(name="streamer_name")
    private String streamerName;

    @Column(name="main_character")
    private String mainCharacter;

    @OneToMany(mappedBy = "streamer", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JsonManagedReference
    private List<LoaCharacter> characters;

    public void addCharacter(LoaCharacter character){
        characters.add(character);
        character.setStreamer(this);
    }
}
