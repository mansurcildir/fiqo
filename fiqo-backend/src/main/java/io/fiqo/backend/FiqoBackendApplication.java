package io.fiqo.backend;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Slf4j
@SpringBootApplication
@EnableMethodSecurity
public class FiqoBackendApplication {

  public FiqoBackendApplication() {
    log.warn("fiqo-backend started successfully!");
  }

  static void main(final @NotNull String[] args) {
    SpringApplication.run(FiqoBackendApplication.class, args);
  }
}
