package com.example.lunaproject.character.controller;

import com.example.lunaproject.character.dto.CharacterDTO;
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

@RestController
@RequestMapping("character")
public class CharacterController {
    private static final Logger logger = LoggerFactory.getLogger(CharacterController.class);
    @Autowired
    CharacterService service;
    @GetMapping("/{mainCharacterName}")
    public String getCharacterSiblings(@PathVariable(required = true) String mainCharacterName) {
        JSONArray character = service.Characters(mainCharacterName);
        ArrayList characters = new ArrayList();
        String mainServer = "루페온";
        for(Object object: character){
            JSONObject jsonObject = (JSONObject) object;
            if(jsonObject.get("ServerName").equals(mainServer)){
                logger.info("find info for : "+object);
                JSONObject characterName = service.characterProfiles((String)jsonObject.get("CharacterName"));
                CharacterDTO dto = new CharacterDTO();
                dto.setServerName((String)characterName.get("ServerName"));
                dto.setCharacterName((String)characterName.get("CharacterName"));
                dto.setCharacterClassName((String) characterName.get("CharacterClassName"));
                dto.setCharacterLevel(((Long)characterName.get("CharacterLevel")).intValue());
                dto.setItemAvgLevel((String)characterName.get("ItemAvgLevel"));
                dto.setCharacterClassName((String)characterName.get("CharacterClassName"));
                dto.setItemMaxLevel((String)characterName.get("ItemMaxLevel"));
                characters.add(dto);
            }
        }
        return characters.toString();
    }
}