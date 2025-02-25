package com.example.lunaproject.streamer.dto;

import com.example.lunaproject.global.utils.GameType;
import lombok.Data;

@Data
public class StreamerRequestDTO {
    private String mainCharacter;
    private String channelId;
    private GameType gameType;
}
