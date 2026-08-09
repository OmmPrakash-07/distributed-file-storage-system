package com.dfss.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())

            .headers(headers ->
                headers.frameOptions(frame -> frame.sameOrigin())
            )

            .authorizeHttpRequests(auth ->
                auth.anyRequest().permitAll()
            )

            .formLogin(form -> form.disable())

            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}