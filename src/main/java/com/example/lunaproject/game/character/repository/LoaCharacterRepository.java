package com.example.lunaproject.game.character.repository;

import com.example.lunaproject.game.character.entity.LoaCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoaCharacterRepository extends JpaRepository<LoaCharacter, String> {
    @Query("select t from LoaCharacter t Where t.characterName= :Charactername")
    Optional<LoaCharacter> findByCharacterName(String charactername);
}
