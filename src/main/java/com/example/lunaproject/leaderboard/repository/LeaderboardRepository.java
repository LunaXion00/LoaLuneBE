package com.example.lunaproject.leaderboard.repository;

import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.entity.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {
    @Query("SELECT l FROM Leaderboard l JOIN l.gameProfile gp WHERE gp.gameType = :gameType ORDER BY l.rank ASC")
    List<Leaderboard> findByGameType(@Param("gameType") GameType gameType);

    void deleteByGameProfile_GameType(GameType gameType);
}
