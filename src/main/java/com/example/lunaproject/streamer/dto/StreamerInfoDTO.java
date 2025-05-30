package com.example.lunaproject.streamer.dto;

import com.example.lunaproject.global.utils.GameType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamerInfoDTO {
    @JsonProperty("streamerName")
    private String streamerName;

    @JsonProperty("channelId")
    private String channelId;

    @JsonProperty("existGame")
    private List<GameType> existGame;

    @JsonProperty("tags")
    private Set<TagDTO> tags;
}
