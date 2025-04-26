package com.example.lunaproject.global.config;

import com.example.lunaproject.api.auth.security.JwtAuthenticationFilter;
import com.example.lunaproject.global.handler.CustomAuthenticationFailureHandler;
import com.example.lunaproject.global.handler.CustomAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final CustomAuthenticationFailureHandler failureHandler;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/**");
    }

    @Bean
    protected SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/admin/login") // GET: 로그인 form 페이지 (뷰 이름: admin-login.html)
                        .loginProcessingUrl("/admin/process-login") // POST 요청 처리 URL
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .invalidSessionUrl("/admin/login")
                )
                .addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);
        return http.build();
    }
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User
                .withUsername(adminUsername)
                .password(adminPassword) // {noop} prefix 때문에 암호화 안 함
                .roles("ADMIN") // ROLE_ADMIN
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}