package oit.is.team4.schedule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // 追加

import oit.is.team4.schedule.mapper.ScheduleMapper;
import oit.is.team4.schedule.model.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.security.Principal;

@Controller
public class ScheduleController {

  private final ScheduleMapper scheduleMapper;

  public ScheduleController(ScheduleMapper scheduleMapper) {
    this.scheduleMapper = scheduleMapper;
  }

  @PostMapping("/addplan")
  public String postAddplan(
      @RequestParam("year") int year,
      @RequestParam("month") int month,
      @RequestParam("day") int day,
      @RequestParam("start_time") String startTime,
      @RequestParam("end_time") String endTime,
      @RequestParam("title") String title,
      Principal principal) { // 【修正】Principalを追加

    Schedule s = new Schedule();
    s.setPlanDate(LocalDate.of(year, month, day));
    s.setStartTime(LocalTime.parse(startTime));
    s.setEndTime(LocalTime.parse(endTime));
    s.setTitle(title);

    String userName = (principal != null) ? principal.getName() : "匿名";
    s.setUserName(userName);

    scheduleMapper.insertPlan(s);

    return "redirect:/schedule/day?date=" + year + "-" + month + "-" + day;
  }

  // 【追加】予定削除機能
  @PostMapping("/schedule/delete")
  public String deletePlan(
      @RequestParam("id") int id,
      @RequestParam("date") String date, // 戻る先の画面用
      Principal principal,
      RedirectAttributes ra) {

    Schedule s = scheduleMapper.selectById(id);
    if (s == null) {
      return "redirect:/schedule/day?date=" + date;
    }

    String currentUserName = (principal != null) ? principal.getName() : "匿名";

    // 所有者チェック
    if (currentUserName.equals(s.getUserName())) {
      scheduleMapper.deleteById(id);
      ra.addFlashAttribute("message", "予定を削除しました");
    } else {
      ra.addFlashAttribute("error", "削除権限がありません");
    }

    return "redirect:/schedule/day?date=" + date;
  }
}
