package oit.is.team4.schedule.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import oit.is.team4.schedule.model.ImageLike;

public interface ImageLikeRepository extends JpaRepository<ImageLike, Long> {
  Optional<ImageLike> findByFilename(String filename);

  @Transactional
  void deleteByFilename(String filename);
}
