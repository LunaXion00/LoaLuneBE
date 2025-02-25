package com.example.lunaproject.api.registry;

import com.example.lunaproject.api.client.GameApiClient;
import com.example.lunaproject.game.character.dto.GameCharacterDTO;
import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.global.utils.GameType;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GameApiClientRegistry {
    private final Map<GameType, GameApiClient<? extends GameCharacterDTO>> clients;

    @Autowired
    public GameApiClientRegistry(List<GameApiClient<?>> clients) {
        this.clients = clients.stream()
                .collect(Collectors.toMap(
                        GameApiClient::getGameType,
                        Function.identity()
                ));
    }
    @SuppressWarnings("unchecked")
    public <T extends GameCharacterDTO> GameApiClient<T> getClient(GameType gameType) {
        GameApiClient<? extends GameCharacterDTO> client = clients.get(gameType);
        return (GameApiClient<T>) client;
    }
}
