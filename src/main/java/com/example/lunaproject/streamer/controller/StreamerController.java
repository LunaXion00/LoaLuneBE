package com.example.lunaproject.streamer.controller;

import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.streamer.dto.*;
import com.example.lunaproject.streamer.service.StreamerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("streamers")
public class StreamerController {
    private final StreamerService service;
    @PostMapping("/upload")
    public ResponseEntity<String> uploadStreamerInfo(@RequestBody StreamerRequestDTO requestDTO) throws IOException {
        try{
            StreamerResponseDTO dto = service.createStreamer(requestDTO);
            return ResponseEntity.ok("스트리머 "+dto.getStreamerName()+"님의 메인 캐릭터 "+requestDTO.getMainCharacter()+"이(가) 등록되었습니다.");
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{streamerName}/{gameType}")
    public ResponseEntity<?> getStreamerInfo(@PathVariable(required = true) String streamerName, @PathVariable GameType gameType) throws IOException{
        try{
            StreamerWithCharacterDTO dto =  service.getStreamerInfo(streamerName, gameType);
            return ResponseEntity.ok().body(dto);
        } catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{channelId}/tags")
    public ResponseEntity<?> updateStreamerTags(@PathVariable(required = true) String channelId, @RequestBody TagRequestDTO tagRequestDTO) throws IOException{
        try{
            service.updateStreamerTags(channelId, tagRequestDTO.getTags());
            return ResponseEntity.ok().body("태그 갱신 완료");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
