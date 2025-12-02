package oit.is.team4.schedule.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ImageMapper {
  @Insert("INSERT INTO image (image_name) VALUES (#{imageName})")
  void insertImage(String imageName);

    // 画像ファイル名の一覧を取得する（テンプレートで表示するため）
  @Select("SELECT image_name FROM image")
  List<String> selectAllImageNames();
}
