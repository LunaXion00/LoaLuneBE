package com.example.lunaproject.leaderboard.service;

import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.repository.LeaderboardRepository;
import com.example.lunaproject.streamer.repository.GameProfileRepository;
import com.example.lunaproject.streamer.repository.StreamerRepository;
import com.example.lunaproject.streamer.service.StreamerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SpringJUnitConfig(LeaderboardCacheInvalidationTest.Config.class)
class LeaderboardCacheInvalidationTest {
    @Autowired
    private LeaderboardUpdaterService updater;
    @Autowired
    private CacheManager redisCacheManager;
    @Autowired
    private PlatformTransactionManager transactionManager;

    private Cache cache;

    @BeforeEach
    void seedCache() {
        cache = Objects.requireNonNull(redisCacheManager.getCache("leaderboard"));
        cache.put(GameType.lostark.name(), "previous lostark board");
        cache.put(GameType.vlrt.name(), "previous valorant board");
    }

    @Test
    void evictsOnlyUpdatedGameAfterCommit() {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            updater.updateLeaderboard(GameType.lostark);

            assertThat(cache.get(GameType.lostark.name(), String.class)).isEqualTo("previous lostark board");
        });

        assertThat(cache.get(GameType.lostark.name())).isNull();
        assertThat(cache.get(GameType.vlrt.name(), String.class)).isEqualTo("previous valorant board");
    }

    @Test
    void keepsCachedBoardWhenOuterTransactionRollsBack() {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            updater.updateLeaderboard(GameType.lostark);
            status.setRollbackOnly();
        });

        assertThat(cache.get(GameType.lostark.name(), String.class)).isEqualTo("previous lostark board");
    }

    @Configuration(proxyBeanMethods = false)
    @EnableCaching
    @EnableTransactionManagement
    static class Config {
        @Bean
        CacheManager redisCacheManager() {
            return new TransactionAwareCacheManagerProxy(new ConcurrentMapCacheManager("leaderboard"));
        }

        @Bean
        PlatformTransactionManager transactionManager() {
            return new TestTransactionManager();
        }

        @Bean
        LeaderboardUpdaterService leaderboardUpdaterService() {
            return new LeaderboardUpdaterService(
                    mock(LeaderboardRepository.class),
                    mock(GameProfileRepository.class),
                    Map.of(),
                    mock(StreamerRepository.class),
                    mock(StreamerService.class));
        }
    }

    private static final class TestTransactionManager extends AbstractPlatformTransactionManager {
        @Override
        protected Object doGetTransaction() {
            Object transaction = TransactionSynchronizationManager.getResource(this);
            return transaction != null ? transaction : new Object();
        }

        @Override
        protected boolean isExistingTransaction(Object transaction) {
            return TransactionSynchronizationManager.hasResource(this);
        }

        @Override
        protected void doBegin(Object transaction, TransactionDefinition definition) {
            TransactionSynchronizationManager.bindResource(this, transaction);
        }

        @Override
        protected void doCommit(DefaultTransactionStatus status) {
        }

        @Override
        protected void doRollback(DefaultTransactionStatus status) {
        }

        @Override
        protected void doCleanupAfterCompletion(Object transaction) {
            TransactionSynchronizationManager.unbindResource(this);
        }
    }
}
