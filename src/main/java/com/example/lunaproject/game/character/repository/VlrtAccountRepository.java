package com.example.lunaproject.game.character.repository;

import com.example.lunaproject.game.character.entity.VlrtAccount;
import com.example.lunaproject.global.utils.GameServer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VlrtAccountRepository extends JpaRepository<VlrtAccount, Long> {
    boolean existsByCharacterName(String charactername);
    VlrtAccount findByCharacterNameAndServer(String characterName, GameServer server);
}
