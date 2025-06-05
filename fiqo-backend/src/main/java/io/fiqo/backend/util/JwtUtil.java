package io.fiqo.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final String accessKey =
      "561744090F30C5564F793305AB783BC96545E88EE42A7ED0CED578C72CE132A3";

  private final String refreshKey =
      "42DCCF1E0B06EF601CF9DCDA0ED3877F4F0DB32FAE243DC5F0C681DB09AFB454";

  public @NotNull UUID getUserUuidFromAccessToken(final @NotNull String token) {
    return this.extractClaim(token, this.accessKey, claims -> UUID.fromString(claims.getSubject()));
  }

  public @NotNull UUID getUserUuidFromRefreshToken(final @NotNull String token) {
    return this.extractClaim(
        token, this.refreshKey, claims -> UUID.fromString(claims.getSubject()));
  }

  public @NotNull String getUsernameFromAccessToken(final @NotNull String token) {
    return this.extractClaim(token, this.accessKey, claims -> claims.get("username", String.class));
  }

  public @NotNull List<String> getUserRoles(final @NotNull String token) {
    final Claims claims = this.extractAllClaims(token, this.accessKey);
    return claims.get("roles", List.class);
  }

  public @NotNull <T> T extractClaim(
      final @NotNull String token,
      final @NotNull String signKey,
      final @NotNull Function<Claims, T> claimsResolver) {
    final Claims claims = this.extractAllClaims(token, signKey);
    return claimsResolver.apply(claims);
  }

  public @NotNull Claims extractAllClaims(
      final @NotNull String token, final @NotNull String signKey) {
    return Jwts.parser()
        .verifyWith(this.getSignKey(signKey))
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public @NotNull String generateToken(
      @NotNull final String signKey,
      final @NotNull String subject,
      final @NotNull Map<String, Object> claims,
      final int expMin) {

    final Instant now = Instant.now();
    final Instant futureInstant = now.plusSeconds(expMin * 60L);
    final ZonedDateTime zonedDateTime = futureInstant.atZone(ZoneId.systemDefault());

    final Date expirationDate = Date.from(zonedDateTime.toInstant());

    return Jwts.builder()
        .header()
        .add(
            Map.of(
                "alg", "HS256",
                "typ", "JWT"))
        .and()
        .subject(subject)
        .claims(claims)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(expirationDate)
        .signWith(this.getSignKey(signKey))
        .compact();
  }

  public @NotNull String generateAccessToken(
      final @NotNull UUID userUuid,
      final @NotNull String username,
      final @NotNull List<String> roles) {
    return this.generateToken(
        this.accessKey, userUuid.toString(), Map.of("username", username, "roles", roles), 5);
  }

  public @NotNull String generateRefreshToken(final @NotNull UUID userUuid) {
    return this.generateToken(this.refreshKey, userUuid.toString(), Map.of(), 7 * 24 * 60);
  }

  private @NotNull SecretKey getSignKey(final @NotNull String signKey) {
    final byte[] keyBytes = Decoders.BASE64.decode(signKey);
    return new SecretKeySpec(keyBytes, "HmacSHA256");
  }
}
