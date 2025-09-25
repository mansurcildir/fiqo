package io.fiqo.backend.account.dto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AccountItem {
  @NotNull
  String getUuid();

  @Nullable
  String getUsername();

  @Nullable
  String getEmail();

  @Nullable
  String getAvatarUrl();
}
