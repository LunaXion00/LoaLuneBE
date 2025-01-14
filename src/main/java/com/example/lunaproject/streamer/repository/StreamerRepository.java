package com.example.lunaproject.streamer.repository;

import com.example.lunaproject.streamer.entity.Streamer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StreamerRepository extends JpaRepository<Streamer, String> {
    Optional<Streamer> findByStreamerName(String streamerName);
}
