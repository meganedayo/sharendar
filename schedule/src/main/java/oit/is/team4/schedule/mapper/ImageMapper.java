package oit.is.team4.schedule.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result; // これが必要です
import org.apache.ibatis.annotations.Results; // これが必要です
import org.apache.ibatis.annotations.Select;

import oit.is.team4.schedule.model.ImageRecord;

@Mapper
public interface ImageMapper {

  @Insert("INSERT INTO image (image_name, scheduled_time) VALUES (#{imageName}, #{scheduledTime})")
  void insertImage(@Param("imageName") String imageName, @Param("scheduledTime") LocalDateTime scheduledTime);

  @Select("SELECT image_name FROM image")
  List<String> selectAllImageNames();

  @Select("SELECT image_name FROM image WHERE CAST(scheduled_time AS DATE) = #{date}")
  List<String> selectImageNamesByDate(@Param("date") LocalDate date);

  /**
   * 指定範囲（開始日時〜終了日時）の画像を取得する
   * databaseカラム名(snake_case)とJavaフィールド名(camelCase)を紐付ける
   */
  @Select("SELECT image_name, scheduled_time FROM image WHERE scheduled_time >= #{start} AND scheduled_time < #{end}")
  @Results({
      @Result(column = "image_name", property = "imageName"),
      @Result(column = "scheduled_time", property = "scheduledTime") // ★ここが最重要
  })
  List<ImageRecord> selectImagesByDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

  @Select({
      "<script>",
      "SELECT image_name, scheduled_time",
      "FROM image",
      "WHERE image_name IN",
      "<foreach item='n' collection='names' open='(' separator=',' close=')'>",
      "#{n}",
      "</foreach>",
      "</script>"
  })
  @Results({
      @Result(column = "image_name", property = "imageName"),
      @Result(column = "scheduled_time", property = "scheduledTime")
  })
  List<ImageRecord> selectImagesByNames(@Param("names") List<String> names);
}
