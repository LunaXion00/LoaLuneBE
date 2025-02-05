package com.example.lunaproject.game.character.controller;

import com.example.lunaproject.game.character.service.LoaCharacterService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("characters")
public class LoaCharacterController {
    private static final Logger logger = LoggerFactory.getLogger(LoaCharacterController.class);
    private final LoaCharacterService service;
}