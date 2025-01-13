package com.example.lunaproject.character.controller;

import com.example.lunaproject.character.dto.CharacterDTO;
import com.example.lunaproject.character.entity.LoaCharacter;
import com.example.lunaproject.character.service.CharacterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("character")
public class CharacterController {
    private static final Logger logger = LoggerFactory.getLogger(CharacterController.class);

    private final CharacterService service;

    @Autowired
    public CharacterController(CharacterService service) {
        this.service = service;
    }

    @GetMapping("/{mainCharacterName}")
    public String getCharacterSiblings(@PathVariable(required = true) String mainCharacterName) {
        JSONArray character = service.Characters(mainCharacterName);
        ArrayList characters = new ArrayList();
        for(Object object: character){
            JSONObject jsonObject = (JSONObject) object;
            logger.info("find info for : "+object);
            JSONObject characterName = service.characterProfiles((String)jsonObject.get("CharacterName"));
            CharacterDTO dto = new CharacterDTO();
            dto.setServerName((String)characterName.get("ServerName"));
            dto.setCharacterName((String)characterName.get("CharacterName"));
            dto.setCharacterClassName((String) characterName.get("CharacterClassName"));
            dto.setCharacterLevel(((Long)characterName.get("CharacterLevel")).intValue());
            dto.setItemLevel((Double)characterName.get("ItemMaxLevel"));
            dto.setCharacterClassName((String)characterName.get("CharacterClassName"));
            characters.add(dto);
            service.addCharacter(dto);
        }
        return characters.toString();
    }
}