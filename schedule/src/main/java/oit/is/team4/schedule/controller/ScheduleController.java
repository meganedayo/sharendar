package oit.is.team4.schedule.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap; // 追加
import java.util.List;
import java.util.Map; // 追加
import java.util.stream.Collectors; // 追加

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import oit.is.team4.schedule.mapper.ScheduleMapper;
import oit.is.team4.schedule.model.Schedule;

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
      @RequestParam("start_time") String startTimeStr,
      @RequestParam("end_time") String endTimeStr,
      @RequestParam("title") String title,
      Principal principal,
      RedirectAttributes ra) { // エラーメッセージ用に追加

    // 時間の文字列をLocalTimeに変換
    LocalTime start = LocalTime.parse(startTimeStr);
    LocalTime end = LocalTime.parse(endTimeStr);

    // 【追加】時間の前後チェック
    // 終了時刻が、開始時刻より「後(After)」でなければエラー
    if (!end.isAfter(start)) {
      ra.addFlashAttribute("error", "終了時刻は開始時刻より後に設定してください。");
      return "redirect:/schedule/day?date=" + year + "-" + month + "-" + day;
    }

    Schedule s = new Schedule();
    s.setPlanDate(LocalDate.of(year, month, day));
    s.setStartTime(start);
    s.setEndTime(end);
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

  // 【重要】非同期更新用API
  // 指定された日付の予定リストをJSONで返す
  // 【修正】非同期更新用API
  // フロントエンド側で処理しやすいようにMapにつめ直して返す
  @GetMapping("/schedule/api/list")
  @ResponseBody
  public List<Map<String, Object>> getScheduleList(@RequestParam("date") String dateString) {
    LocalDate date;
    try {
      date = LocalDate.parse(dateString);
    } catch (DateTimeParseException e) {
      String[] parts = dateString.split("-");
      int y = Integer.parseInt(parts[0]);
      int m = Integer.parseInt(parts[1]);
      int d = Integer.parseInt(parts[2]);
      date = LocalDate.of(y, m, d);
    }

    List<Schedule> schedules = scheduleMapper.selectByDate(date);

    // ScheduleオブジェクトをJSON用のMapに変換
    return schedules.stream().map(s -> {
      Map<String, Object> map = new HashMap<>();
      map.put("id", s.getId());
      map.put("title", s.getTitle());
      map.put("userName", s.getUserName());

      // 時間を文字列として確定させる（JSでのパースミスを防ぐ）
      map.put("startTime", s.getStartTime().toString()); // "10:00" など
      map.put("endTime", s.getEndTime().toString());

      // 何時の枠に表示すべきか(hour)をサーバー側で計算して送る
      map.put("hour", s.getStartTime().getHour());

      return map;
    }).collect(Collectors.toList());
  }
}
