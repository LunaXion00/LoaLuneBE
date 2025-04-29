package com.example.lunaproject.streamer.controller;

import com.example.lunaproject.global.utils.GameType;
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
    @GetMapping("/{streamerName}/{gameType}")
    public ResponseEntity<?> getStreamerInfo(@PathVariable(required = true) String streamerName, @PathVariable GameType gameType) throws IOException{
        try{
            StreamerWithCharacterDTO dto =  service.getStreamerInfo(streamerName, gameType);
            return ResponseEntity.ok().body(dto);
        } catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllStreamers() throws IOException{
        try{
            List<StreamerInfoDTO> dtos = service.getAllStreamerInfo();
            return ResponseEntity.ok().body(dtos);
        } catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
