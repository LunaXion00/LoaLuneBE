package com.example.lunaproject.streamer.entity;

import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.game.character.entity.LoaCharacter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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

    @Column(name="channel_id")
    private String channelId;

    @OneToMany(mappedBy = "streamer", cascade = CascadeType.ALL)
    @Builder.Default
    private List<GameProfile> gameProfiles = new ArrayList<>();

    @Column(name="channel_image_url", length=511)
    private String channelImageUrl;

    @ManyToMany
    @JoinTable(
            name = "streamer_tags", // 연결 테이블 이름
            joinColumns = @JoinColumn(name = "streamer_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();


}
