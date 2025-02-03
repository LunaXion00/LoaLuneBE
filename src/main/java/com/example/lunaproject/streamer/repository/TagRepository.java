package com.example.lunaproject.streamer.repository;

import com.example.lunaproject.streamer.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {
    Boolean existsByTagName(String tagName);
}
