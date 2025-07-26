package com.example.lunaproject.api.admin.controller;

import com.example.lunaproject.global.handler.GlobalExceptionHandler;
import com.example.lunaproject.global.utils.GameType;
import com.example.lunaproject.leaderboard.service.LeaderboardUpdaterService;
import com.example.lunaproject.leaderboard.service.UpdateLeaderboardService;
import com.example.lunaproject.leaderboard.strategy.config.RankingDetailStrategyConfig;
import com.example.lunaproject.leaderboard.strategy.detail.RankingDetailStrategy;
import com.example.lunaproject.streamer.service.StreamerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig(AdminLeaderboardRefreshTest.Config.class)
class AdminLeaderboardRefreshTest {
    private static final String REFRESH_URL = "/admin/leaderboards/{gameType}/refresh";

    @Autowired
    private AdminController adminController;
    @Autowired
    private LeaderboardUpdaterService updater;
    @Autowired
    private UpdateLeaderboardService refreshService;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        reset(updater);
        mvc = MockMvcBuilders.standaloneSetup(adminController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @EnumSource(value = GameType.class, names = {"lostark", "vlrt"})
    void administratorCanRefreshSupportedGame(GameType gameType) throws Exception {
        authenticate("ROLE_ADMIN");

        mvc.perform(post(REFRESH_URL, gameType.name())).andExpect(status().isNoContent());

        verify(updater).updateLeaderboard(gameType);
    }

    @Test
    void requiresAuthentication() throws Exception {
        mvc.perform(post(REFRESH_URL, "lostark")).andExpect(status().isUnauthorized());

        verifyNoInteractions(updater);
    }

    @Test
    void requiresAdministratorRole() throws Exception {
        authenticate("ROLE_USER");

        mvc.perform(post(REFRESH_URL, "lostark")).andExpect(status().isForbidden());

        verifyNoInteractions(updater);
    }

    @ParameterizedTest
    @ValueSource(strings = {"common", "unknown"})
    void rejectsUnsupportedGame(String gameType) throws Exception {
        authenticate("ROLE_ADMIN");

        mvc.perform(post(REFRESH_URL, gameType)).andExpect(status().isBadRequest());

        verifyNoInteractions(updater);
    }

    @Test
    void allowsRetryAfterRefreshFails() throws Exception {
        authenticate("ROLE_ADMIN");
        doThrow(new IllegalArgumentException("Provider failure"))
                .doNothing().when(updater).updateLeaderboard(GameType.lostark);

        mvc.perform(post(REFRESH_URL, "lostark")).andExpect(status().isInternalServerError());
        mvc.perform(post(REFRESH_URL, "lostark")).andExpect(status().isNoContent());
    }

    @Test
    void sharesRefreshGuardBetweenManualAndScheduledRuns() throws Exception {
        var started = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        doAnswer(invocation -> {
            started.countDown();
            assertTrue(release.await(5, TimeUnit.SECONDS));
            return null;
        }).when(updater).updateLeaderboard(GameType.lostark);

        try (var executor = Executors.newSingleThreadExecutor()) {
            var firstRequest = executor.submit(() -> {
                authenticate("ROLE_ADMIN");
                try {
                    return mvc.perform(post(REFRESH_URL, "lostark"));
                } finally {
                    SecurityContextHolder.clearContext();
                }
            });

            try {
                assertTrue(started.await(5, TimeUnit.SECONDS));
                authenticate("ROLE_ADMIN");
                mvc.perform(post(REFRESH_URL, "lostark")).andExpect(status().isConflict());
                refreshService.updateAllLeaderboards();
            } finally {
                release.countDown();
            }

            firstRequest.get(5, TimeUnit.SECONDS).andExpect(status().isNoContent());
        }
        verify(updater).updateLeaderboard(GameType.lostark);
        verify(updater).updateLeaderboard(GameType.vlrt);
    }

    private static void authenticate(String role) {
        var authentication = new TestingAuthenticationToken("test-user", null, role);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Configuration(proxyBeanMethods = false)
    @EnableMethodSecurity
    @Import({UpdateLeaderboardService.class, RankingDetailStrategyConfig.class})
    static class Config {
        @Bean
        LeaderboardUpdaterService leaderboardUpdaterService() {
            return mock(LeaderboardUpdaterService.class);
        }

        @Bean
        RankingDetailStrategy lostarkRankings() {
            return rankingStrategy(GameType.lostark);
        }

        @Bean
        RankingDetailStrategy valorantRankings() {
            return rankingStrategy(GameType.vlrt);
        }

        @Bean
        AdminController adminController(UpdateLeaderboardService refreshService) {
            return new AdminController(mock(StreamerService.class), Map.of(), refreshService);
        }

        private static RankingDetailStrategy rankingStrategy(GameType gameType) {
            var strategy = mock(RankingDetailStrategy.class);
            when(strategy.getGameType()).thenReturn(gameType);
            return strategy;
        }
    }
}
