package com.example.lunaproject.streamer.controller;

import com.example.lunaproject.character.entity.LoaCharacter;
import com.example.lunaproject.streamer.dto.StreamerRequestDTO;
import com.example.lunaproject.streamer.service.StreamerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("streamer")
public class StreamerController {
    private final StreamerService service;
    @PostMapping("/upload")
    public ResponseEntity<String> uploadStreamerInfo(@RequestBody StreamerRequestDTO requestDTO) throws IOException {
        try{
            service.createStreamer(requestDTO);
            return ResponseEntity.ok("스트리머 "+requestDTO.getChannelId()+"님의 메인 캐릭터 "+requestDTO.getMainCharacter()+"이(가) 등록되었습니다.");
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{streamerName}")
    public ResponseEntity<String> getStreamerInfo(@PathVariable(required = true) String streamerName) throws IOException{
        try{
            List<LoaCharacter> characterList = service.getStreamerInfo(streamerName);
            return ResponseEntity.ok().body(characterList.stream().map(LoaCharacter::getCharacterName).collect(Collectors.toList()).toString());
        } catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Streamer Finding Error"+e.getMessage());
        }
    }
}
