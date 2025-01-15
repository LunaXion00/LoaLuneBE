package com.example.lunaproject.streamer.repository;


import com.example.lunaproject.streamer.entity.Streamer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;

import java.util.Optional;

@RequiredArgsConstructor
public class StreamerRepositoryImpl implements StreamerCustomRepository{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Streamer get(String streamerName) {
        // JPQL을 사용한 커스텀 쿼리 예제
        return entityManager.createQuery(
                        "SELECT s FROM Streamer s WHERE s.streamerName = :streamerName", Streamer.class)
                .setParameter("streamerName", streamerName)
                .getSingleResult();
    }
    @Override
    public Streamer get(Long streamerId){
        return entityManager.createQuery(
                "select s from Streamer s where s.id = :streamerId", Streamer.class)
                .setParameter("streamerId", streamerId)
                .getSingleResult();
    }

}
