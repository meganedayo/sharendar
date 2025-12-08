package oit.is.team4.schedule.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.team4.schedule.mapper.ImageMapper;
import oit.is.team4.schedule.mapper.ScheduleMapper;
import oit.is.team4.schedule.model.ImageRecord;
import oit.is.team4.schedule.model.Schedule;

@Controller
public class ScheduleDayController {

  private final ScheduleMapper scheduleMapper;

  @Autowired
  private ImageMapper imageMapper;

  public ScheduleDayController(ScheduleMapper scheduleMapper) {
    this.scheduleMapper = scheduleMapper;
  }

  @GetMapping("/schedule/day")
  public String showDay(
      @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String dateString,
      Model model) {

    // 日付解析
    LocalDate date = parseFlexibleDate(dateString);

    // LocalDate で検索
    List<Schedule> list = scheduleMapper.selectByDate(date);
    System.out.println("selectByDate(" + date + ") size=" + list.size());

    // 0〜23 時をすべて空リストで初期化
    Map<Integer, List<Schedule>> plansByHour = new HashMap<>();
    for (int h = 0; h < 24; h++) {
      plansByHour.put(h, new ArrayList<>());
    }

    // 予定を時間ごとに格納
    for (Schedule s : list) {
      int hour = s.getStartTime().getHour();
      plansByHour.get(hour).add(s);
    }

    model.addAttribute("date", date.toString());
    model.addAttribute("hours", IntStream.range(0, 24).boxed().toList());
    model.addAttribute("plansByHour", plansByHour);
    model.addAttribute("plans", list); // デバッグ用

    // 画像（そのまま）
    Map<Integer, List<String>> imagesByHour = new HashMap<>();

    try {
      LocalDateTime start = date.atStartOfDay();
      LocalDateTime end = date.plusDays(1).atStartOfDay();

      List<ImageRecord> records = imageMapper.selectImagesByDate(start, end);

      if (records != null) {
        for (ImageRecord r : records) {
          if (r == null || r.getScheduledTime() == null || r.getImageName() == null) {
            continue;
          }
          int hour = r.getScheduledTime().getHour();
          imagesByHour.computeIfAbsent(hour, k -> new ArrayList<>()).add(r.getImageName());
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    model.addAttribute("imagesByHour", imagesByHour);

    return "schedule_day";
  }

  // 日付解析（柔軟対応）
  private LocalDate parseFlexibleDate(String dateString) {
    try {
      return LocalDate.parse(dateString); // 2025-12-01 形式
    } catch (DateTimeParseException e) {
      String[] parts = dateString.split("-");
      if (parts.length != 3) {
        throw e;
      }
      int y = Integer.parseInt(parts[0]);
      int m = Integer.parseInt(parts[1]);
      int d = Integer.parseInt(parts[2]);
      return LocalDate.of(y, m, d);
    }
  }
}
