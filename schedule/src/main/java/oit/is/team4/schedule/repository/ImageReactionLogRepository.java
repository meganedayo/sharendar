package oit.is.team4.schedule.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import oit.is.team4.schedule.model.ImageReactionLog;
import java.util.Optional;

public interface ImageReactionLogRepository extends JpaRepository<ImageReactionLog, Long> {
  List<ImageReactionLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

  boolean existsByUserNameAndFilename(String userName, String filename);

  Optional<ImageReactionLog> findByUserNameAndFilename(String userName, String filename);

  @Query("""
      select r.filename
      from ImageReactionLog r
      where r.createdAt >= :start and r.createdAt < :end
      group by r.filename
      having count(distinct r.userName) >= :minUsers
      """)
  List<String> findHotFilenames(
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end,
      @Param("minUsers") long minUsers
  );

  @Transactional
  void deleteByFilename(String filename);
}
