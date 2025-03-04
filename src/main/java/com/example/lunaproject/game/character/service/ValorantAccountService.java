package com.example.lunaproject.game.character.service;

import com.example.lunaproject.api.valorant.client.ValorantAccountApiClient;
import com.example.lunaproject.game.character.dto.VlrtAccountDTO;
import com.example.lunaproject.streamer.dto.StreamerRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ValorantAccountService {
    private final ValorantAccountApiClient apiClient;
    public List<VlrtAccountDTO> getAccountInfo(StreamerRequestDTO requestDTO){
        List<VlrtAccountDTO> dtos = apiClient.createCharacterList(requestDTO);
        return dtos;
    }
}
