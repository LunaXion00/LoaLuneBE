package com.example.lunaproject.game.character.service;

import com.example.lunaproject.global.utils.GameType;

public interface CharacterService {
    void updateCharacters(String streamerName);
    GameType getGameType();
}
