package com.example.lunaproject.character.entity;

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
    private int id;

    @Column(name = "server")
    private String serverName;

    @Column(name = "character_name")
    private String characterName;

    @Column(name = "character_level")
    private int characterLevel;

    @Column(name = "class")
    private String characterClassName;

    @Column(name = "item_level")
    private String itemLevel;

}
