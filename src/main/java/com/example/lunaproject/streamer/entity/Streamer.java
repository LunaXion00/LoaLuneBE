package com.example.lunaproject.streamer.entity;

import com.example.lunaproject.game.character.entity.LoaCharacter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column(name="channel_image_url")
    private String channelImageUrl;

    @ManyToMany
    @JoinTable(
            name = "streamer_tags", // 연결 테이블 이름
            joinColumns = @JoinColumn(name = "streamer_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public void addCharacter(LoaCharacter character){
        characters.add(character);
        character.setStreamer(this);
    }
    public void editMainCharacter(String mainCharacter){
        this.mainCharacter = mainCharacter;
    }
    public void createCharacter(List<LoaCharacter> characterList){
        characterList.stream()
                .peek(character->character.setStreamer(this))
                .forEach(characters::add);
        this.mainCharacter = characterList.get(0).getCharacterName();
    }
}
