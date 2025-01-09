package com.example.lunaproject.character.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
//@Table(name= "LoaCharacters")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class LoaCharacter{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "loacharacter_id")
    private long id;

    @Column(nullable = false)
    private String serverName;

    @Column(nullable = false)
    private String characterName;

    @Column(nullable = false)
    private int characterLevel;

    @Column(nullable = false)
    private String characterClassName;

    @Column(nullable = false)
    private String itemMaxLevel;
    @Column(nullable = false)
    private String itemAvgLevel;

}
