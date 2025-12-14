package oit.is.team4.schedule.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.team4.schedule.service.ReactionHighlightService;

@Controller
public class CalendarController {

  private final ReactionHighlightService reactionHighlightService;

  public CalendarController(ReactionHighlightService reactionHighlightService) {
    this.reactionHighlightService = reactionHighlightService;
  }

  @GetMapping("/calendar")
  public String showCalendar(@RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer month,
      Model model) {

    // 1. 表示する年月を決定（指定がなければ現在日時）
    LocalDate targetDate;
    if (year == null || month == null) {
      targetDate = LocalDate.now().withDayOfMonth(1); // 今月の1日
    } else {
      targetDate = LocalDate.of(year, month, 1);
    }

    // 2. 画面表示用のデータをセット
    model.addAttribute("targetYear", targetDate.getYear());
    model.addAttribute("targetMonth", targetDate.getMonthValue());

    // 3. 次月・前月のリンク用データをセット
    LocalDate prev = targetDate.minusMonths(1);
    LocalDate next = targetDate.plusMonths(1);
    model.addAttribute("prevYear", prev.getYear());
    model.addAttribute("prevMonth", prev.getMonthValue());
    model.addAttribute("nextYear", next.getYear());
    model.addAttribute("nextMonth", next.getMonthValue());

    // 4. カレンダーのマス目（カレンダーロジック）を作成
    // 週ごとのリスト（List<List<Integer>>）を作成します。nullは空白マスです。
    List<List<Integer>> calendarMatrix = generateCalendarMatrix(targetDate);
    model.addAttribute("calendarMatrix", calendarMatrix);

    YearMonth yearMonth = YearMonth.from(targetDate);
    Set<LocalDate> highlightDateSet = reactionHighlightService.getHighlightDatesForMonth(yearMonth);
    // テンプレートで扱いやすいよう, 日(1〜31)のセットに変換
    Set<Integer> highlightDays = highlightDateSet.stream()
        .map(LocalDate::getDayOfMonth)
        .collect(Collectors.toSet());
    model.addAttribute("highlightDays", highlightDays);

    return "calendar.html";
  }

  /**
   * 指定された年月のカレンダー配列（6行x7列のようなリスト）を生成する補助メソッド
   */
  private List<List<Integer>> generateCalendarMatrix(LocalDate date) {
    List<List<Integer>> matrix = new ArrayList<>();
    List<Integer> week = new ArrayList<>();

    // その月の長さ（28~31）と、1日の曜日を取得
    YearMonth yearMonth = YearMonth.from(date);
    int lengthOfMonth = yearMonth.lengthOfMonth();
    DayOfWeek firstDayOfWeek = date.getDayOfWeek();

    // 日曜始まりのカレンダーにするためのオフセット計算
    // DayOfWeekは月曜=1...日曜=7。日曜始まりなら日曜(7)を0、月曜(1)を1...としたい。
    int emptyDays = firstDayOfWeek.getValue();
    if (emptyDays == 7)
      emptyDays = 0; // 日曜日の場合は前に空白なし

    // 1. 月初めの空白セルを埋める
    for (int i = 0; i < emptyDays; i++) {
      week.add(null);
    }

    // 2. 日付を埋める
    for (int day = 1; day <= lengthOfMonth; day++) {
      week.add(day);

      // 土曜日(リストサイズが7)になったら次の週へ
      if (week.size() == 7) {
        matrix.add(week);
        week = new ArrayList<>();
      }
    }

    // 3. 最終週の残りを空白で埋める
    if (week.size() > 0) {
      while (week.size() < 7) {
        week.add(null);
      }
      matrix.add(week);
    }

    return matrix;
  }
}
