package com.example.lunaproject.global.utils;

import lombok.Getter;

@Getter
public enum GameType {
    lostark("로스트아크"),
    ;
    private final String type;

    GameType(String type){
        this.type = type;
    }
}
