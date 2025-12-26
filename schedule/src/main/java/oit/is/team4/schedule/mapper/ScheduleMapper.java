package oit.is.team4.schedule.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import oit.is.team4.schedule.model.Schedule;

@Mapper
public interface ScheduleMapper {

  // 予定登録
  // 【修正】user_name を追加
  @Insert("""
      INSERT INTO schedule (plan_date, start_time, end_time, title, user_name)
      VALUES (#{planDate}, #{startTime}, #{endTime}, #{title}, #{userName})
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertPlan(Schedule schedule);

  // 全件取得（デバッグ用）
  // 【修正】user_name AS userName を追加
  @Select("""
      SELECT
        id,
        plan_date  AS planDate,
        start_time AS startTime,
        end_time   AS endTime,
        title,
        user_name  AS userName
      FROM schedule
      ORDER BY plan_date, start_time
      """)
  List<Schedule> selectAll();

  // ★ 日付指定で取得（LocalDate）
  // 【修正】user_name AS userName を追加
  @Select("""
      SELECT
        id,
        plan_date  AS planDate,
        start_time AS startTime,
        end_time   AS endTime,
        title,
        user_name  AS userName
      FROM schedule
      WHERE plan_date = #{date}
      ORDER BY start_time
      """)
  List<Schedule> selectByDate(LocalDate date);

  // ID指定
  // 【修正】user_name AS userName を追加
  @Select("""
      SELECT
        id,
        plan_date  AS planDate,
        start_time AS startTime,
        end_time   AS endTime,
        title,
        user_name  AS userName
      FROM schedule
      WHERE id = #{id}
      """)
  Schedule selectById(int id);

  // 【追加】削除用メソッド
  @Delete("DELETE FROM schedule WHERE id = #{id}")
  void deleteById(int id);
}
