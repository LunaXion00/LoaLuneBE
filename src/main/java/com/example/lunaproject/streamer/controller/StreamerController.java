package com.example.lunaproject.streamer.controller;

import com.example.lunaproject.streamer.dto.StreamerRequestDTO;
import com.example.lunaproject.streamer.service.StreamerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

@RequiredArgsConstructor
@RestController
@RequestMapping("streamer")
public class StreamerController {
    private final StreamerService service;
    @PostMapping("/upload")
    public ResponseEntity<String> uploadStreamerInfo(@RequestBody StreamerRequestDTO requestDTO) throws IOException {
        try{
            service.createStreamer(requestDTO);
            return ResponseEntity.ok("스트리머 "+requestDTO.getStreamerName()+"님의 메인 캐릭터 "+requestDTO.getMainCharacter()+"이(가) 등록되었습니다.");
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{streamerName}")
    public ResponseEntity<String> getStreamerInfo(@RequestBody StreamerRequestDTO dto){
        return ResponseEntity.badRequest().body("not defined yet");
    }
}
