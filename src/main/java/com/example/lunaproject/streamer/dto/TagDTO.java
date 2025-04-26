package com.example.lunaproject.streamer.dto;

import com.example.lunaproject.global.utils.GameType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagDTO {
    private String tagName;
    private GameType gameType = GameType.common;
}
