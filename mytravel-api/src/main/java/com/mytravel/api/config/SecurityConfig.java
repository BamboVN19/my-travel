package com.mytravel.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytravel.api.dto.ErrorResponse;
import com.mytravel.api.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.Instant;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.findAndRegisterModules();
    return mapper;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint((request, response, authException) -> {
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.setContentType("application/json;charset=UTF-8");
              ErrorResponse errorResponse = ErrorResponse.builder()
                  .timestamp(Instant.now())
                  .status(HttpStatus.UNAUTHORIZED.value())
                  .error(HttpStatus.UNAUTHORIZED.name())
                  .message("Token JWT không hợp lệ hoặc đã hết hạn")
                  .path(request.getRequestURI())
                  .build();
              response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            })
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**", "/uploads/**", "/error").permitAll()
            .anyRequest().authenticated()
        );

    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
