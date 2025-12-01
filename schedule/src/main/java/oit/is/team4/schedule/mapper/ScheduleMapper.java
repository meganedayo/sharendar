package oit.is.team4.schedule.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import oit.is.team4.schedule.model.Schedule;

@Mapper
public interface ScheduleMapper {

  // 予定登録
  @Insert("""
      INSERT INTO schedule (plan_date, start_time, title)
      VALUES (#{planDate}, #{startTime}, #{title})
      """)
  void insertPlan(Schedule schedule);

  // 全件取得（必要なら）
  @Select("""
      SELECT
        id,
        plan_date  AS planDate,
        start_time AS startTime,
        title
      FROM schedule
      ORDER BY plan_date, start_time
      """)
  List<Schedule> selectAll();

  // 日付指定で取得（日別ページ用）
  @Select("""
      SELECT
        id,
        plan_date  AS planDate,
        start_time AS startTime,
        title
      FROM schedule
      WHERE plan_date = #{date}
      ORDER BY start_time
      """)
  List<Schedule> selectByDate(LocalDate date);

  // ID指定（将来用）
  @Select("""
      SELECT
        id,
        plan_date  AS planDate,
        start_time AS startTime,
        title
      FROM schedule
      WHERE id = #{id}
      """)
  Schedule selectById(int id);
}
