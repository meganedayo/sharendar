package oit.is.team4.schedule.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageMapper {
  @Insert("INSERT INTO image (image_name) VALUES (#{imageName})")
  void insertImage(String imageName);
}
