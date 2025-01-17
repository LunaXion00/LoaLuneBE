package com.example.lunaproject.streamer.repository;

import com.example.lunaproject.streamer.entity.Streamer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StreamerRepository extends JpaRepository<Streamer, Long>, StreamerCustomRepository {
    Boolean existsByStreamerName(String streamerName);
    Boolean existsByChannelId(String channelId);
}
