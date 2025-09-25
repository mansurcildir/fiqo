package io.fiqo.backend.account;

import io.fiqo.backend.account.dto.AccountItem;
import io.fiqo.backend.result.ResponseFactory;
import io.fiqo.backend.result.Result;
import io.fiqo.backend.user.dto.UserDetails;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final @NotNull AccountService accountService;
  private final @NotNull ResponseFactory responseFactory;

  @GetMapping({"/google", "/google/"})
  public @NotNull ResponseEntity<Result> getGoogleAccounts(
      final @NotNull Authentication authentication) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<AccountItem> accounts =
        this.accountService.getGoogleAccounts(userDetails.userUuid());

    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "accountsFetched", accounts));
  }

  @GetMapping({"/github", "/github/"})
  public @NotNull ResponseEntity<Result> getGithubAccounts(
      final @NotNull Authentication authentication) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<AccountItem> accounts =
        this.accountService.getGithubAccounts(userDetails.userUuid());

    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "accountsFetched", accounts));
  }

  @DeleteMapping({"/{accountUuid}", "/{accountUuid}/"})
  public @NotNull ResponseEntity<Result> deleteAccount(
      final @NotNull Authentication authentication,
      @PathVariable final @NotNull String accountUuid) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    this.accountService.deleteAccount(userDetails.userUuid(), UUID.fromString(accountUuid));

    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "accountDeleted"));
  }
}
