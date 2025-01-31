package com.example.lunaproject.streamer.dto;

import com.example.lunaproject.character.dto.CharacterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class StreamerWithCharacterDTO {
    private String streamerName;
    private String mainCharacter;
    private String channelId;
    private String channelImageUrl;
    private List<CharacterDTO> characters;
    private Set<TagDTO> tags;
}
