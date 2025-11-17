package io.fiqo.backend.usage;

import io.fiqo.backend.result.ResponseFactory;
import io.fiqo.backend.result.Result;
import io.fiqo.backend.user.dto.UserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/daily-usages")
@RequiredArgsConstructor
public class DailyUsageController {

  private final @NotNull ResponseFactory responseFactory;
  private final @NotNull DailyUsageService dailyUsageService;

  @PostMapping
  public @NotNull ResponseEntity<Result> createDailyUsage(
      final @NotNull Authentication authentication) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    this.dailyUsageService.createDailyUsage(userDetails.userUuid());

    return ResponseEntity.status(HttpStatus.OK)
        .body(this.responseFactory.success(HttpStatus.OK.value(), "dailyUsageCreated"));
  }

  @GetMapping
  public @NotNull ResponseEntity<Result> getDailyUsages(
      final @NotNull Authentication authentication, final @RequestParam String year) {
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    final List<DailyUsageItem> dailyUsages =
        this.dailyUsageService.getDailyUsagesByYear(userDetails.userUuid(), Integer.parseInt(year));

    return ResponseEntity.status(HttpStatus.OK)
        .body(
            this.responseFactory.success(HttpStatus.OK.value(), "dailyUsagesFetched", dailyUsages));
  }
}
