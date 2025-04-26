package com.example.lunaproject.streamer.entity;

import com.example.lunaproject.global.utils.GameType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tagName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameType gameType = GameType.common;

}
