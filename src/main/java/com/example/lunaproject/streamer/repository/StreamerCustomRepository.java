package com.example.lunaproject.streamer.repository;

import com.example.lunaproject.streamer.entity.Streamer;

public interface StreamerCustomRepository {
    Streamer get(String streamerName);
    Streamer get(Long streamerId);
}
