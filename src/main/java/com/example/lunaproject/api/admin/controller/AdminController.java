package com.example.lunaproject.api.admin.controller;

import com.example.lunaproject.api.admin.dto.ModifyCharacterDTO;
import com.example.lunaproject.game.character.dto.GameCharacterDTO;
import com.example.lunaproject.game.character.service.CharacterService;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.streamer.dto.StreamerRequestDTO;
import com.example.lunaproject.streamer.dto.StreamerResponseDTO;
import com.example.lunaproject.streamer.dto.TagRequestDTO;
import com.example.lunaproject.streamer.service.StreamerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final StreamerService streamerService;
    private final Map<GameType, CharacterService> serviceMap;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/streamers/upload")
    public ResponseEntity<String> uploadStreamerInfo(@RequestBody StreamerRequestDTO requestDTO, @RequestHeader("Authorization")String auth) throws IOException {
        try{
            StreamerResponseDTO dto = streamerService.createStreamer(requestDTO);
            return ResponseEntity.ok("스트리머 "+dto.getStreamerName()+"님의 메인 캐릭터 "+requestDTO.getMainCharacter()+"이(가) 등록되었습니다.");
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{channelId}/tags")
    public ResponseEntity<?> updateStreamerTags(@PathVariable(required = true) String channelId, @RequestBody TagRequestDTO tagRequestDTO) throws IOException{
        try{
            streamerService.updateStreamerTags(channelId, tagRequestDTO.getTags());
            return ResponseEntity.ok().body("태그 갱신 완료");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{channelId}/character")
    public ResponseEntity<?> modifyCharactersInfo(@PathVariable(required = true) String channelId, @RequestBody ModifyCharacterDTO modifyCharacterDTO) throws IOException{
        try{
            CharacterService characterService = serviceMap.get(modifyCharacterDTO.getGameType());
            characterService.modifyCharacterInfo(channelId, modifyCharacterDTO);
            return ResponseEntity.ok().body("캐릭터 수정 완료");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}