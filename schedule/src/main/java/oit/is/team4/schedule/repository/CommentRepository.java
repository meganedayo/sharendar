package oit.is.team4.schedule.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import oit.is.team4.schedule.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findByFilenameOrderByCreatedAtDesc(String filename);

  @Transactional
  void deleteByFilename(String filename);
}
