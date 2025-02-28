package com.example.lunaproject.streamer.repository;

import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.streamer.entity.GameProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameProfileRepository extends JpaRepository<GameProfile, Long> {
    List<GameProfile> findAllByGameType(GameType gameType);
}
