package com.example.lunaproject.streamer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagRequestDTO {
    private List<TagDTO> tags;
}
