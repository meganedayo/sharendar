package oit.is.team4.schedule.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.team4.schedule.mapper.ScheduleMapper;
import oit.is.team4.schedule.model.Schedule;

@Controller
public class ScheduleDayController {

  private final ScheduleMapper scheduleMapper;

  public ScheduleDayController(ScheduleMapper scheduleMapper) {
    this.scheduleMapper = scheduleMapper;
  }

  /**
   * 日別ページ
   * 例：/schedule/day?date=2025-12-1 や 2025-12-01 を受け付ける
   */
  @GetMapping("/schedule/day")
  public String showDay(@RequestParam("date") String dateString, Model model) {

    // ---- yyyy-MM-dd / yyyy-M-d 両方を許す ----
    LocalDate date = parseFlexibleDate(dateString);

    // この日の予定一覧を取得
    List<Schedule> list = scheduleMapper.selectByDate(date);

    // 時間ごと(0〜23時)に分類（1時間1件想定）
    Map<Integer, Schedule> plansByHour = new HashMap<>();
    for (Schedule s : list) {
      int hour = s.getStartTime().getHour();
      plansByHour.put(hour, s);
    }

    // Viewへ渡す
    model.addAttribute("date", date.toString()); // 2025-12-01 形式
    model.addAttribute("hours", IntStream.range(0, 24).boxed().toList());
    model.addAttribute("plansByHour", plansByHour);

    return "schedule_day.html"; // templates/daySchedule.html
  }

  /**
   * 2025-12-1, 2025-12-01 の両方を LocalDate に変換するヘルパー
   */
  private LocalDate parseFlexibleDate(String dateString) {
    try {
      // まず標準の yyyy-MM-dd で試す
      return LocalDate.parse(dateString);
    } catch (DateTimeParseException e) {
      // ダメなら yyyy-M-d を手動で分割して組み立てる
      String[] parts = dateString.split("-");
      if (parts.length != 3) {
        throw e; // 想定外フォーマットならそのまま投げる
      }
      int y = Integer.parseInt(parts[0]);
      int m = Integer.parseInt(parts[1]);
      int d = Integer.parseInt(parts[2]);
      return LocalDate.of(y, m, d);
    }
  }
}
