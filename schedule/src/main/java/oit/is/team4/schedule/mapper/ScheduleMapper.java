package oit.is.team4.schedule.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

import oit.is.team4.schedule.model.Schedule;

@Mapper
public interface ScheduleMapper {

  @Insert("INSERT INTO schedule (plan_date, start_time, title) VALUES (#{planDate}, #{startTime}, #{title})")
  void insertPlan(Schedule schedule);

  @Select("SELECT * FROM schedule")
  List<Schedule> selectAll();
}
