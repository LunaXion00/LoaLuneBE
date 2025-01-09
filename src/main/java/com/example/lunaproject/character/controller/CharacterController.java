package com.example.lunaproject.character.controller;

import com.example.lunaproject.character.service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.json.JSONArray;

import java.util.ArrayList;

@RestController
@RequestMapping("character")
public class CharacterController {
    @Autowired
    CharacterService service;
    @GetMapping("/{characterName}")
    public String getCharacterSiblings(@PathVariable(required = true) String characterName) {
        JSONArray character = service.Characters(characterName);
        ArrayList characters = new ArrayList();
    }
}