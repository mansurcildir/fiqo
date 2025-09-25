package io.fiqo.backend.auth;

import java.security.SecureRandom;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class AuthUtil {
  private static final @NotNull String USER_PRE = "user";
  private static final @NotNull String EMAIL_SUF = "@fiqo.io";
  private static final int USERNAME_DIGIT_COUNT = 8;
  private static final int DIGIT_BOUND = 10;

  public static @NotNull String generateRandomUsername() {
    final SecureRandom secureRandom = new SecureRandom();
    final StringBuilder sb = new StringBuilder(USER_PRE);

    for (int i = 0; i < USERNAME_DIGIT_COUNT; i++) {
      final int digit = secureRandom.nextInt(DIGIT_BOUND);
      sb.append(digit);
    }
    return sb.toString();
  }

  public static @NotNull String generateEmail(final @NotNull String username) {
    return username + EMAIL_SUF;
  }
}
