package oit.is.team4.schedule.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import oit.is.team4.schedule.model.PendingUser;

public interface PendingUserRepository extends JpaRepository<PendingUser, Long> {
  Optional<PendingUser> findByUsername(String username);
}
