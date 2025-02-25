package com.example.lunaproject.streamer.repository;

import com.example.lunaproject.streamer.entity.GameProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameProfileRepository extends JpaRepository<GameProfile, Long> {
}
