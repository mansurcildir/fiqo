package io.fiqo.backend.auth;

import static io.fiqo.backend.auth.AuthUtil.generateEmail;
import static io.fiqo.backend.auth.AuthUtil.generateRandomUsername;

import io.fiqo.backend.account.Account;
import io.fiqo.backend.account.AccountRepository;
import io.fiqo.backend.account.AccountService;
import io.fiqo.backend.account.dto.AccountForm;
import io.fiqo.backend.auth.dto.AccountType;
import io.fiqo.backend.auth.dto.OAuthUserInfo;
import io.fiqo.backend.user.dto.AuthResponse;
import io.fiqo.backend.user.dto.UserRegister;
import io.fiqo.backend.util.JwtUtil;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("googleAuthService")
@Transactional
@RequiredArgsConstructor
public class GoogleAuthService implements OAuthService {
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
    final String avatarUrl = userInfo.avatarUrl();

    final Optional<Account> accountOpt =
        this.accountRepository.findBySubjectIdAndAccountType(
            subjectId, AccountType.GOOGLE.toString());

    if (accountOpt.isPresent()) {
      final Account account = this.updateAccountInfo(accountOpt.get(), userInfo);
      return this.authService.createAuthResponse(account.getUser());
    }

    final UserRegister userRegister =
        UserRegister.builder().email(email).username(username).password(null).build();

    final AuthResponse authResponse = this.authService.register(userRegister);
    final UUID userUuid = this.jwtUtil.getUserUuidFromAccessToken(authResponse.accessToken());

    this.accountService.connectAccount(
        userUuid, new AccountForm(subjectId, null, accountEmail, avatarUrl), AccountType.GOOGLE);

    return authResponse;
  }

  @Override
  public void connect(final @NotNull UUID userUuid, final @NotNull OAuthUserInfo userInfo) {
    final String subjectId = userInfo.sub();
    final String email = userInfo.email();
    final String avatarUrl = userInfo.avatarUrl();

    this.accountService.connectAccount(
        userUuid, new AccountForm(subjectId, null, email, avatarUrl), AccountType.GOOGLE);
  }

  @Override
  public @Nullable String getPrimaryEmail(final @NotNull String token) {
    throw new NotImplementedException();
  }

  private @NotNull Account updateAccountInfo(
      final @NotNull Account account, final @NotNull OAuthUserInfo userInfo) {

    account.setEmail(userInfo.email());
    account.setAvatarUrl(userInfo.avatarUrl());
    return this.accountRepository.save(account);
  }
}
