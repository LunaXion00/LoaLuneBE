package com.example.lunaproject.streamer.repository;

import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.streamer.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {
    Boolean existsByTagName(String tagName);
    Optional<Tag> findByTagName(String tagName);
    Optional<Tag> findByTagNameAndGameType(String tagName, GameType gameType);

}
