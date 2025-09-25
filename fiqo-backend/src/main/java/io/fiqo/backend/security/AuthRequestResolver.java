package io.fiqo.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthRequestResolver implements OAuth2AuthorizationRequestResolver {

  private final @NotNull OAuth2AuthorizationRequestResolver defaultResolver;

  public AuthRequestResolver(final @NotNull ClientRegistrationRepository repo) {
    this.defaultResolver =
        new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
  }

  @Override
  public @Nullable OAuth2AuthorizationRequest resolve(final @NotNull HttpServletRequest request) {
    return this.customize(this.defaultResolver.resolve(request), request);
  }

  @Override
  public @Nullable OAuth2AuthorizationRequest resolve(
      final @NotNull HttpServletRequest request, final @NotNull String clientRegistrationId) {
    return this.customize(this.defaultResolver.resolve(request, clientRegistrationId), request);
  }

  private @Nullable OAuth2AuthorizationRequest customize(
      final @Nullable OAuth2AuthorizationRequest req,
      final @NotNull HttpServletRequest httpRequest) {

    if (req == null) {
      return null;
    }

    final String action = httpRequest.getParameter("action");
    if (action != null) {
      httpRequest.getSession().setAttribute("action", action);
    }

    final String token = httpRequest.getParameter("token");
    if (token != null) {
      httpRequest.getSession().setAttribute("token", token);
    }

    return req;
  }
}
