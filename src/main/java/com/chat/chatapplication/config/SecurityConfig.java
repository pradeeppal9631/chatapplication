package com.chat.chatapplication.config;


import com.chat.chatapplication.jwtsecurity.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth


                        .requestMatchers(
                                "/auth/login",
                                "/auth/register",
                                "/users/register",
                                "/",
                                "/index.html",
                                "/login.html",
                                "/ws/**"
                        ).permitAll()


                        .requestMatchers("/users/**").authenticated()
                        .requestMatchers("/chat/history/**").authenticated()


                        .requestMatchers("/redis/**").permitAll()
                        .requestMatchers("/debug/ws-users").permitAll()

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

}
