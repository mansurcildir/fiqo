package io.fiqo.backend.auth;

import static io.fiqo.backend.auth.AuthUtil.generateEmail;
import static io.fiqo.backend.auth.AuthUtil.generateRandomUsername;

import io.fiqo.backend.account.Account;
import io.fiqo.backend.account.AccountRepository;
import io.fiqo.backend.account.AccountService;
import io.fiqo.backend.account.dto.AccountForm;
import io.fiqo.backend.auth.dto.AccountType;
import io.fiqo.backend.auth.dto.GithubEmail;
import io.fiqo.backend.auth.dto.OAuthUserInfo;
import io.fiqo.backend.user.dto.AuthResponse;
import io.fiqo.backend.user.dto.UserRegister;
import io.fiqo.backend.util.JwtUtil;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service("githubAuthService")
@Transactional
@RequiredArgsConstructor
public class GithubAuthService implements OAuthService {

  @Value("${spring.security.oauth2.client.provider.github.email-uri}")
  private String emailUri;

  private final @NotNull AccountRepository accountRepository;
  private final @NotNull AccountService accountService;
  private final @NotNull AuthService authService;
  private final @NotNull JwtUtil jwtUtil;

  @Override
  public @NotNull AuthResponse login(final @NotNull OAuthUserInfo userInfo) {
    final String subjectId = userInfo.sub();
    final String username = generateRandomUsername();
    final String email = generateEmail(username);
    final String accountEmail = Objects.requireNonNull(userInfo.email());
    final String accountUsername = Objects.requireNonNull(userInfo.username());
    final String avatarUrl = userInfo.avatarUrl();

    final Optional<Account> accountOpt =
        this.accountRepository.findBySubjectIdAndAccountType(
            subjectId, AccountType.GITHUB.toString());

    if (accountOpt.isPresent()) {
      final Account account =
          this.updateAccountInfo(accountOpt.get(), accountEmail, accountUsername);
      return this.authService.createAuthResponse(account.getUser());
    }

    final UserRegister userRegister =
        UserRegister.builder().username(username).email(email).password(null).build();

    final AuthResponse authResponse = this.authService.register(userRegister);
    final UUID userUuid = this.jwtUtil.getUserUuidFromAccessToken(authResponse.accessToken());

    this.accountService.connectAccount(
        userUuid, new AccountForm(subjectId, null, accountEmail, avatarUrl), AccountType.GITHUB);

    return authResponse;
  }

  @Override
  public void connect(final @NotNull UUID userUuid, final @NotNull OAuthUserInfo userInfo) {
    final String subjectId = userInfo.sub();
    final String username = userInfo.username();
    final String email = userInfo.email();
    final String avatarUrl = userInfo.avatarUrl();

    this.accountService.connectAccount(
        userUuid, new AccountForm(subjectId, username, email, avatarUrl), AccountType.GITHUB);
  }

  @Override
  public @Nullable String getPrimaryEmail(final @NotNull String token) {
    try {
      final RestTemplate restTemplate = new RestTemplate();

      final HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(token);
      headers.setAccept(List.of(MediaType.APPLICATION_JSON));

      final HttpEntity<Void> entity = new HttpEntity<>(headers);

      final ResponseEntity<List<GithubEmail>> response =
          restTemplate.exchange(
              this.emailUri, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

      final List<GithubEmail> emails = response.getBody();
      if (emails == null || emails.isEmpty()) {
        return null;
      }

      return emails.stream()
          .filter(email -> email.primary() && email.verified())
          .map(GithubEmail::email)
          .findFirst()
          .orElse(null);
    } catch (final @NotNull HttpClientErrorException ex) {
      log.warn(
          "{} {} Failed to fetch Github primary email.",
          ex.getStatusCode(),
          ex.getResponseBodyAsString());
      return null;
    }
  }

  private @NotNull Account updateAccountInfo(
      final @NotNull Account account,
      final @Nullable String email,
      final @NotNull String username) {

    account.setUsername(username);
    account.setEmail(email);
    return this.accountRepository.save(account);
  }
}
