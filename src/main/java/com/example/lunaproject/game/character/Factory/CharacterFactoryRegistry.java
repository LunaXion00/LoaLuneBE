package com.example.lunaproject.game.character.Factory;

import com.example.lunaproject.game.character.dto.GameCharacterDTO;
import com.example.lunaproject.game.character.entity.GameCharacter;
import com.example.lunaproject.global.utils.GameType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CharacterFactoryRegistry {
    private final Map<GameType, CharacterFactory<? extends GameCharacter, ?>> factoryMap;

    @Autowired
    public CharacterFactoryRegistry(List<CharacterFactory<?, ?>> factories) {
        this.factoryMap = factories.stream()
                .collect(Collectors.toMap(
                        CharacterFactory::getGameType, // getGameType()으로 키 추출
                        Function.identity()
                ));
    }

    @SuppressWarnings("unchecked")
    public <T extends GameCharacter, D extends GameCharacterDTO>
    CharacterFactory<T, D> getFactory(GameType type) {
        CharacterFactory<?, ?> factory = factoryMap.get(type);
        return (CharacterFactory<T, D>) factory;
    }
}