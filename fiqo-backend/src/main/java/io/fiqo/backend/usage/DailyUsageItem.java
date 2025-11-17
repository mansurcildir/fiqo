package io.fiqo.backend.usage;

import org.jetbrains.annotations.NotNull;

public interface DailyUsageItem {
  @NotNull
  String getCreatedAt();

  @NotNull
  String getBandwidth();
}
