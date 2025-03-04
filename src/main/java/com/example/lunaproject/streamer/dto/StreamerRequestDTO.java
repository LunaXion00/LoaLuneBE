package com.example.lunaproject.streamer.dto;

import com.example.lunaproject.global.utils.GameServer;
import com.example.lunaproject.global.utils.GameType;
import lombok.Data;

import javax.annotation.Nullable;

@Data
public class StreamerRequestDTO {
    private String mainCharacter;
    private String channelId;
    private GameType gameType;
    @Nullable
    private GameServer gameServer;
}
