package com.example.lunaproject.leaderboard.controller;

import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.dto.BaseLeaderboardResDTO;
import com.example.lunaproject.leaderboard.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/{gameType}/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {
    private final LeaderboardService leaderboardService;
    @GetMapping
    public ResponseEntity<List<? extends BaseLeaderboardResDTO>> getLeaderboard(@PathVariable GameType gameType){
        List<? extends BaseLeaderboardResDTO> leaderboard = leaderboardService.getLeaderboard(gameType);
        return ResponseEntity.ok().body(leaderboard);
    }
}
