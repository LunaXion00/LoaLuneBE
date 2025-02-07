package com.example.lunaproject.leaderboard.repository;

import com.example.lunaproject.leaderboard.entity.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {
    List<Leaderboard> findByGameTypeOrderByRankAsc(String gameType);
}
