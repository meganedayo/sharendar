package oit.is.team4.schedule.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import oit.is.team4.schedule.model.ImageReactionLog;
import java.util.Optional;

public interface ImageReactionLogRepository extends JpaRepository<ImageReactionLog, Long> {
  List<ImageReactionLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

  boolean existsByUserNameAndFilename(String userName, String filename);

  Optional<ImageReactionLog> findByUserNameAndFilename(String userName, String filename);
}
