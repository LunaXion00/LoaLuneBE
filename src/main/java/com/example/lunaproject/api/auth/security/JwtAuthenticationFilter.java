package com.example.lunaproject.api.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            String token = parseBearerToken(req);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsername(token);
                AbstractAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null,
                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);
            }
        } catch (Exception e) {
            // 로그 남기고 넘기기 (SecurityContext는 비어있음)
            logger.warn("JWT 인증 실패: {}", e.getMessage());
        }
        chain.doFilter(req, res);
    }

    private String parseBearerToken(HttpServletRequest req) {
        String bearerTokens = req.getHeader("Authorization");
        if (StringUtils.hasText(bearerTokens) && bearerTokens.startsWith("Bearer ")) {
            return bearerTokens.substring(7);
        }
        return null;
    }
}