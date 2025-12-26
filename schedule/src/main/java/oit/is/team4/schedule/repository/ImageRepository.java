package oit.is.team4.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import oit.is.team4.schedule.model.ImageEntity;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
  // ファイル名から画像データを検索
  ImageEntity findByImageName(String imageName);
}
