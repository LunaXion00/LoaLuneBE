package com.example.lunaproject.character.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CharacterInfoDTO<T>{
    private String error;
    private List<T> data;
}
