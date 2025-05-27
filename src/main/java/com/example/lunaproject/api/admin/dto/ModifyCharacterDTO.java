package com.example.lunaproject.api.admin.dto;

import com.example.lunaproject.global.utils.GameServer;
import com.example.lunaproject.global.utils.GameType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.annotation.Nullable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ModifyCharacterDTO {
    private GameType gameType;

    @Nullable
    private GameServer gameServer;

    private String beforeCharacterName;
    private String afterCharacterName;
}