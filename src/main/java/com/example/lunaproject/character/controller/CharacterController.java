package com.example.lunaproject.character.controller;

import com.example.lunaproject.character.dto.CharacterDTO;
import com.example.lunaproject.character.entity.LoaCharacter;
import com.example.lunaproject.character.service.CharacterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("character")
public class CharacterController {
    private static final Logger logger = LoggerFactory.getLogger(CharacterController.class);

    private final CharacterService service;

    @PostMapping("/{mainCharacterName}")
    public String getCharacterSiblings(@PathVariable(required = true) String mainCharacterName) throws IOException {
        JSONArray character = service.Characters(mainCharacterName);
        ArrayList characters = new ArrayList();
        for(Object object: character){
            JSONObject jsonObject = (JSONObject) object;
            JSONObject characterName = service.characterDetails((String)jsonObject.get("CharacterName"));
            CharacterDTO dto = new ObjectMapper().readValue(characterName.toString(), CharacterDTO.class);
            characters.add(dto);
            service.addCharacterToRepository(dto);
        }
        return characters.toString();
    }

//    @GetMapping("/{CharacterName}")
//    public LoaCharacter<?> retrieveCharacter(){
//
//    }
}