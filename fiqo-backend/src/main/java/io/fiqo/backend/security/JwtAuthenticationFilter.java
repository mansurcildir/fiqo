package io.fiqo.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fiqo.backend.result.ResponseFactory;
import io.fiqo.backend.result.Result;
import io.fiqo.backend.user.dto.UserDetails;
import io.fiqo.backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private static final @NotNull String ROLE_PREFIX = "ROLE_";
  private static final int BEARER_INDEX = 7;

  private final @NotNull JwtUtil jwtUtil;
  private final @NotNull ResponseFactory responseFactory;

  @Override
  protected void doFilterInternal(
      final @NotNull HttpServletRequest request,
      final @NotNull HttpServletResponse response,
      final @NotNull FilterChain filterChain)
      throws IOException {

    final String token = this.extractToken(request);

    try {
      if (token != null) {
        final String username = this.jwtUtil.getUsernameFromAccessToken(token);
        final UUID userUuid = this.jwtUtil.getUserUuidFromAccessToken(token);
        final List<String> roles = this.jwtUtil.getUserRoles(token);
        final List<GrantedAuthority> authorities =
            roles.stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .collect(Collectors.toList());

        final UserDetails userDetails =
            UserDetails.builder().userUuid(userUuid).username(username).build();

        final UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        authenticationToken.setDetails(userUuid);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }
      filterChain.doFilter(request, response);
    } catch (final @NotNull Exception e) {
      log.info("Error while authorization: {}", e.getMessage());
      this.unAuthorize(e.getMessage(), response);
    }
  }

  private void unAuthorize(final @NotNull String msg, final @NotNull HttpServletResponse response)
      throws IOException {
    final Result result = this.responseFactory.error(HttpStatus.UNAUTHORIZED.value(), msg);
    final ObjectMapper objectMapper = new ObjectMapper();
    final String jsonResponse = objectMapper.writeValueAsString(result);

    response.setContentType("application/json");
    response.getWriter().write(jsonResponse);
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
  }

  private @Nullable String extractToken(final @NotNull HttpServletRequest request) {
    final String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      final String token = header.substring(BEARER_INDEX).trim();
      return token.equals("null") ? null : token;
    }
    return null;
  }
}
