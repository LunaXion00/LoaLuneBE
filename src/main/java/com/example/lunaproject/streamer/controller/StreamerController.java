package com.example.lunaproject.streamer.controller;

import com.example.lunaproject.character.dto.CharacterDTO;
import com.example.lunaproject.streamer.dto.*;
import com.example.lunaproject.streamer.service.StreamerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("streamers")
public class StreamerController {
    private final StreamerService service;
    @PostMapping("/upload")
    public ResponseEntity<String> uploadStreamerInfo(@RequestBody StreamerRequestDTO requestDTO) throws IOException {
        try{
            StreamerDTO dto = service.createStreamer(requestDTO);
            return ResponseEntity.ok("스트리머 "+dto.getStreamerName()+"님의 메인 캐릭터 "+requestDTO.getMainCharacter()+"이(가) 등록되었습니다.");
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{streamerName}/details")
    public ResponseEntity<?> getStreamerInfo(@PathVariable(required = true) String streamerName) throws IOException{
        try{
            StreamerWithCharacterDTO dto =  service.getStreamerInfo(streamerName);
            return ResponseEntity.ok().body(dto);
        } catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{streamerName}/update-characters")
    public ResponseEntity<String> updateStreamerCharacters(@PathVariable(required = true) String streamerName) throws IOException{
        try{
            service.updateStreamerCharacters(streamerName);
            return ResponseEntity.ok().body(streamerName+"님의 캐릭터 정보가 갱신되었습니다.");
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{channelId}/tags")
    public ResponseEntity<?> updateStreamerTags(@PathVariable(required = true) String channelId, @RequestBody TagRequestDTO tagRequestDTO) throws IOException{
        try{
            service.updateStreamerTags(channelId, tagRequestDTO.getTags());
            return ResponseEntity.ok().body(null);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
