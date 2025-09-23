package io.fiqo.backend.security;

import io.fiqo.backend.result.ResponseFactory;
import io.fiqo.backend.util.JwtUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(
      final @NotNull HttpSecurity http,
      final @NotNull JwtUtil jwtUtil,
      final @NotNull ResponseFactory responseFactory)
      throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(this::cors)
        .authorizeHttpRequests(this::authorizeHttpRequests)
        .addFilterBefore(
            new JwtAuthenticationFilter(jwtUtil, responseFactory),
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public @NotNull PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  private void cors(final @NotNull CorsConfigurer<HttpSecurity> corsConfigurer) {
    corsConfigurer.configurationSource(this.configurationSource());
  }

  private @NotNull CorsConfigurationSource configurationSource() {
    final var configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of("*"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedMethods(
        List.of(
            HttpMethod.GET.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.OPTIONS.name(),
            HttpMethod.HEAD.name()));
    configuration.setAllowedHeaders(List.of("*"));

    final var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  private void authorizeHttpRequests(
      final @NotNull AuthorizeHttpRequestsConfigurer<HttpSecurity>
                  .AuthorizationManagerRequestMatcherRegistry
              auth) {
    auth.requestMatchers("/v1/auth/register", "/v1/auth/login", "/v1/auth/refresh")
        .permitAll()
        .anyRequest()
        .authenticated();
  }
}
