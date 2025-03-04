package com.example.lunaproject.game.character.controller;

import com.example.lunaproject.game.character.dto.VlrtAccountDTO;
import com.example.lunaproject.game.character.service.ValorantAccountService;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.streamer.dto.StreamerRequestDTO;
import com.example.lunaproject.streamer.dto.StreamerResponseDTO;
import com.example.lunaproject.streamer.dto.StreamerWithCharacterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("vlrt")
public class ValorantCharacterController {
    private final ValorantAccountService service;
    @PostMapping("/upload")
    public ResponseEntity<String> uploadStreamerInfo(@RequestBody StreamerRequestDTO requestDTO) throws IOException {
        try{
            List<VlrtAccountDTO> dto = service.getAccountInfo(requestDTO);
            return ResponseEntity.ok("");
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
