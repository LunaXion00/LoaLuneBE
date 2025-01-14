package com.example.lunaproject.streamer.service;

import com.example.lunaproject.streamer.dto.StreamerRequestDTO;
import com.example.lunaproject.streamer.entity.Streamer;
import com.example.lunaproject.streamer.repository.StreamerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
public class StreamerService {
    private final StreamerRepository streamerRepository;

    public void uploadStreamer(StreamerRequestDTO requestDTO){
        String streamerName = requestDTO.getStreamerName();

        boolean exists = streamerRepository.findByStreamerName(streamerName).isPresent();
        if(exists) throw new IllegalArgumentException("이미 "+streamerName+"(이)라는 스트리머가 DB에 등록되어있습니다.");
        Streamer streamer = Streamer.builder()
                .streamerName(streamerName)
                .mainCharacter(requestDTO.getMainCharacter())
                .characters(new ArrayList<>())
                .build();
        streamerRepository.save(streamer);
    }
}
