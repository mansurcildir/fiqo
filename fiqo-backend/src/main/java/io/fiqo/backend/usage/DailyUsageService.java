package io.fiqo.backend.usage;

import io.fiqo.backend.user.User;
import io.fiqo.backend.user.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DailyUsageService {

  private final @NotNull UserRepository userRepository;
  private final @NotNull DailyUsageRepository dailyUsageRepository;

  public void calculateDailyUsages() {
    final List<User> users = this.userRepository.findAll();

    for (final @NotNull User user : users) {
      this.calculateDailyUsage(user);
    }
  }

  private void calculateDailyUsage(final @NotNull User user) {
    final DailyUsage dailyUsage = new DailyUsage();
    dailyUsage.setUuid(UUID.randomUUID());
    dailyUsage.setBandwidth(user.getDailyBandwidth());
    dailyUsage.setUser(user);
    this.dailyUsageRepository.save(dailyUsage);

    user.setDailyBandwidth(0L);
    this.userRepository.save(user);
  }

  public @NotNull List<DailyUsageItem> getDailyUsagesByYear(
      final @NotNull UUID userUuid, final int year) {
    return this.dailyUsageRepository.findAllByUserUuidAndYear(userUuid, year);
  }
}
