package oit.is.team4.schedule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.team4.schedule.mapper.ScheduleMapper;
import oit.is.team4.schedule.model.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class ScheduleController {

  private final ScheduleMapper scheduleMapper;

  public ScheduleController(ScheduleMapper scheduleMapper) {
    this.scheduleMapper = scheduleMapper;
  }

  @GetMapping("/addplan")
  public String addplan() {
    return "sample1"; // 予定追加画面
  }

  @PostMapping("/addplan")
  public String postAddplan(
      @RequestParam("year") int year,
      @RequestParam("month") int month,
      @RequestParam("day") int day,
      @RequestParam("time") String time,
      @RequestParam("title") String title) {

    Schedule s = new Schedule();
    s.setPlanDate(LocalDate.of(year, month, day));
    s.setStartTime(LocalTime.parse(time));
    s.setTitle(title);

    scheduleMapper.insertPlan(s);

    return "redirect:/calendar?success";
  }
}
