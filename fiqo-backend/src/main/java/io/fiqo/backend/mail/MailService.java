package io.fiqo.backend.mail;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MailService {

  private final @NotNull JavaMailSender mailSender;

  public void sendPasswordRecovery(final @NotNull String email, final @NotNull String text) {
    final SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setSubject("Reset Your Password");
    message.setText(text);
    this.mailSender.send(message);
  }
}
