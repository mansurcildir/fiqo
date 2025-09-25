package io.fiqo.backend.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountForm {
  private @NotNull String subjectId;
  private @Nullable String username;
  private @Nullable String email;
  private @Nullable String avatarUrl;
}
