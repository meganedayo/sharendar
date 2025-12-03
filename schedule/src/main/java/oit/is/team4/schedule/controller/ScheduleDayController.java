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
  public String showDay(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String dateString,
      Model model) {

    LocalDate date = parseFlexibleDate(dateString);

    // 予定の取得
    List<Schedule> list = scheduleMapper.selectByDate(date);
    Map<Integer, Schedule> plansByHour = new HashMap<>();
    for (Schedule s : list) {
      int hour = s.getStartTime().getHour();
      plansByHour.put(hour, s);
    }

    model.addAttribute("date", date.toString());
    model.addAttribute("hours", IntStream.range(0, 24).boxed().toList());
    model.addAttribute("plansByHour", plansByHour);

    // 画像の取得
    Map<Integer, List<String>> imagesByHour = new HashMap<>();
    try {
      // その日の00:00:00 から 翌日の00:00:00 までを検索範囲とする
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

  private LocalDate parseFlexibleDate(String dateString) {
    try {
      return LocalDate.parse(dateString);
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
