package com.example.lunaproject.game.character.repository;

import com.example.lunaproject.game.character.entity.LoaCharacter;
import com.example.lunaproject.streamer.entity.Streamer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoaCharacterRepository extends JpaRepository<LoaCharacter, Long> {
    @Query("select t from LoaCharacter t Where t.characterName= :Charactername")
    Optional<LoaCharacter> findByCharacterName(String charactername);
//    Optional<LoaCharacter> findTopByStreamerOrderByItemLevelDesc(Streamer streamer);
}
