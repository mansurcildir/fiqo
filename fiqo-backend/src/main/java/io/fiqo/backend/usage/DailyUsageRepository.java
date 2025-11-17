package io.fiqo.backend.usage;

import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DailyUsageRepository extends JpaRepository<DailyUsage, Long> {

  @NotNull
  @Query(
"""
    select
      date(du.createdAt) as createdAt,
      sum(du.bandwidth) as bandwidth
    from DailyUsage du
    where du.user.uuid = :userUuid
      and extract(year from du.createdAt) = :year
    group by date(du.createdAt)
    order by date(du.createdAt)
""")
  List<DailyUsageItem> findAllByUserUuidAndYear(@NotNull UUID userUuid, int year);
}
