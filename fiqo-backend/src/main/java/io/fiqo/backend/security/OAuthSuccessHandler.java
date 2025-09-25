package io.fiqo.backend.security;

import io.fiqo.backend.auth.OAuthService;
import io.fiqo.backend.auth.dto.OAuthUserInfo;
import io.fiqo.backend.user.dto.AuthResponse;
import io.fiqo.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

  @Value("${spring.security.oauth2.client.registration.google.redirect-uri-login}")
  private String googleRedirectUriLogin;

  @Value("${spring.security.oauth2.client.registration.google.redirect-uri-connect}")
  private String googleRedirectUriConnect;

  @Value("${spring.security.oauth2.client.registration.github.redirect-uri-login}")
  private String githubRedirectUriLogin;

  @Value("${spring.security.oauth2.client.registration.github.redirect-uri-connect}")
  private String githubRedirectUriConnect;

  private static final @NotNull String PROVIDER_GOOGLE = "google";
  private static final @NotNull String PROVIDER_GITHUB = "github";
  private static final @NotNull String ACTION_LOGIN = "login";
  private static final @NotNull String ACTION_CONNECT = "connect";

  private final @NotNull JwtUtil jwtUtil;
  private final @NotNull OAuthService googleAuthService;
  private final @NotNull OAuthService githubAuthService;
  private final @NotNull TemporaryTokenStore tokenStore;
  private final @NotNull OAuth2AuthorizedClientService authorizedClientService;

  private final @NotNull MessageSource messageSource;

  public OAuthSuccessHandler(
      @Qualifier("googleAuthService") final @NotNull OAuthService googleAuthService,
      @Qualifier("githubAuthService") final @NotNull OAuthService githubAuthService,
      final @NotNull JwtUtil jwtUtil,
      final @NotNull TemporaryTokenStore tokenStore,
      final @NotNull OAuth2AuthorizedClientService authorizedClientService,
      final @NotNull MessageSource messageSource) {
    this.jwtUtil = jwtUtil;
    this.googleAuthService = googleAuthService;
    this.githubAuthService = githubAuthService;
    this.tokenStore = tokenStore;
    this.authorizedClientService = authorizedClientService;
    this.messageSource = messageSource;
  }

  @Override
  public void onAuthenticationSuccess(
      final @NotNull HttpServletRequest request,
      final @NotNull HttpServletResponse response,
      final @NotNull Authentication authentication)
      throws IOException {

    final HttpSession httpSession = request.getSession();
    final String action = (String) httpSession.getAttribute("action");
    httpSession.removeAttribute("action");

    final OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
    final String provider = oauthToken.getAuthorizedClientRegistrationId();

    final OAuth2User oAuth2User = oauthToken.getPrincipal();
    final Map<String, Object> attributes = oAuth2User.getAttributes();
    final String accessToken = this.extractAccessToken(oauthToken);

    final String email =
        PROVIDER_GITHUB.equals(provider)
            ? this.githubAuthService.getPrimaryEmail(accessToken)
            : (String) attributes.get("email");

    final OAuthUserInfo userInfo = this.mapToOAuthUserInfo(attributes, provider, email);
    final String sessionId = UUID.randomUUID().toString();

    final String redirectUrl =
        switch (action) {
          case ACTION_LOGIN -> this.login(userInfo, provider, sessionId);
          case ACTION_CONNECT -> this.connect(userInfo, provider, httpSession);
          default -> throw new IllegalStateException("Unknown action: " + action);
        };

    response.sendRedirect(redirectUrl);
  }

  private @NotNull String login(
      final @NotNull OAuthUserInfo userInfo,
      final @NotNull String provider,
      final @NotNull String sessionId) {

    try {
      final AuthResponse authResponse;

      return switch (provider) {
        case PROVIDER_GOOGLE -> {
          authResponse = this.googleAuthService.login(userInfo);
          this.tokenStore.put(sessionId, authResponse);
          yield this.googleRedirectUriLogin
              + "?session_id="
              + URLEncoder.encode(sessionId, StandardCharsets.UTF_8);
        }
        case PROVIDER_GITHUB -> {
          authResponse = this.githubAuthService.login(userInfo);
          this.tokenStore.put(sessionId, authResponse);
          yield this.githubRedirectUriLogin
              + "?session_id="
              + URLEncoder.encode(sessionId, StandardCharsets.UTF_8);
        }
        default -> throw new IllegalStateException("Unknown provider: " + provider);
      };
    } catch (final @NotNull Exception ex) {
      final String message =
          Objects.requireNonNull(
              this.messageSource.getMessage(
                  ex.getMessage(), null, ex.getMessage(), Locale.getDefault()));
      final String encodedError = URLEncoder.encode(message, StandardCharsets.UTF_8);

      return switch (provider) {
        case PROVIDER_GOOGLE -> this.googleRedirectUriLogin + "?error=" + encodedError;
        case PROVIDER_GITHUB -> this.githubRedirectUriLogin + "?error=" + encodedError;
        default -> throw new IllegalStateException("Unknown provider: " + provider);
      };
    }
  }

  private @NotNull String connect(
      final @NotNull OAuthUserInfo userInfo,
      final @NotNull String provider,
      final @NotNull HttpSession httpSession) {

    try {
      final UUID userUuid =
          this.jwtUtil.getUserUuidFromAccessToken((String) httpSession.getAttribute("token"));

      return switch (provider) {
        case PROVIDER_GOOGLE -> {
          this.googleAuthService.connect(userUuid, userInfo);
          yield this.googleRedirectUriConnect;
        }
        case PROVIDER_GITHUB -> {
          this.githubAuthService.connect(userUuid, userInfo);
          yield this.githubRedirectUriConnect;
        }
        default -> throw new IllegalStateException("Unknown provider: " + provider);
      };
    } catch (final @NotNull Exception ex) {
      final String message =
          Objects.requireNonNull(
              this.messageSource.getMessage(
                  ex.getMessage(), null, ex.getMessage(), Locale.getDefault()));
      final String encodedError = URLEncoder.encode(message, StandardCharsets.UTF_8);

      return switch (provider) {
        case PROVIDER_GOOGLE -> this.googleRedirectUriConnect + "?error=" + encodedError;
        case PROVIDER_GITHUB -> this.githubRedirectUriConnect + "?error=" + encodedError;
        default -> throw new IllegalStateException("Unknown provider: " + provider);
      };
    }
  }

  private @NotNull String extractAccessToken(final @NotNull OAuth2AuthenticationToken token) {
    final OAuth2AuthorizedClient client =
        this.authorizedClientService.loadAuthorizedClient(
            token.getAuthorizedClientRegistrationId(), token.getName());
    return client.getAccessToken().getTokenValue();
  }

  private @NotNull OAuthUserInfo mapToOAuthUserInfo(
      final @NotNull Map<String, Object> attributes,
      final @NotNull String provider,
      final @Nullable String email) {
    if (PROVIDER_GOOGLE.equals(provider)) {
      return new OAuthUserInfo(
          (String) attributes.get("sub"), null, email, (String) attributes.get("picture"));
    } else if (PROVIDER_GITHUB.equals(provider)) {
      return new OAuthUserInfo(
          attributes.get("id").toString(),
          (String) attributes.get("login"),
          email,
          (String) attributes.get("avatar_url"));
    } else {
      throw new IllegalArgumentException("Unknown provider: " + provider);
    }
  }
}
