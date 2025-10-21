package io.fiqo.backend.verification;

import io.fiqo.backend.exception.ItemNotFoundException;
import io.fiqo.backend.mail.EmailForm;
import io.fiqo.backend.mail.MailService;
import io.fiqo.backend.user.User;
import io.fiqo.backend.user.UserRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class VerificationService {
  @Value("${fiqo.panel-base-url}")
  private String panelBaseUrl;

  private static final int EXPIRATION_MINUTE = 10;

  private final @NotNull MailService mailService;
  private final @NotNull UserRepository userRepository;
  private final @NotNull VerificationRepository verificationRepository;

  public void createVerification(
      final @NotNull EmailForm emailForm, final @NotNull VerificationType verificationType) {

    final User user =
        this.userRepository
            .findByEmail(emailForm.email())
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    final String hashedCode = DigestUtils.sha256Hex(code);

    final Verification verification = new Verification();

    verification.setUuid(UUID.randomUUID());
    verification.setType(verificationType);
    verification.setCode(hashedCode);
    verification.setExpiresAt(Instant.now().plus(Duration.ofMinutes(EXPIRATION_MINUTE)));
    verification.setUser(user);

    this.verificationRepository.save(verification);

    final String text = this.panelBaseUrl + "/reset-password/" + code;
    this.mailService.sendPasswordRecovery(emailForm.email(), text);
  }
}
