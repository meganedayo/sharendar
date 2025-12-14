package oit.is.team4.schedule.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import oit.is.team4.schedule.model.ImageReactionLog;
import oit.is.team4.schedule.repository.ImageReactionLogRepository;

/**
 * reactionlog を元に、
 * 同じ日付に同じ画像へ 2 人以上のユーザがリアクションした日をハイライト対象とするサービス。
 */
@Service
public class ReactionHighlightService {

  private final ImageReactionLogRepository imageReactionLogRepository;

  public ReactionHighlightService(ImageReactionLogRepository imageReactionLogRepository) {
    this.imageReactionLogRepository = imageReactionLogRepository;
  }

  /**
   * 指定年月の中で、ハイライトすべき LocalDate の集合を返す。
   */
  public Set<LocalDate> getHighlightDatesForMonth(YearMonth yearMonth) {
    // その月の 1 日 0:00 〜 翌月 1 日 0:00 までのログを取得
    LocalDate monthStart = yearMonth.atDay(1);
    LocalDate monthEnd = yearMonth.plusMonths(1).atDay(1);

    LocalDateTime start = monthStart.atStartOfDay();
    LocalDateTime end = monthEnd.atStartOfDay();

    List<ImageReactionLog> logs =
        imageReactionLogRepository.findByCreatedAtBetween(start, end);

    // 日付 -> (ファイル名 -> ユーザ集合) で集計
    Map<LocalDate, Map<String, Set<String>>> agg = new HashMap<>();

    for (ImageReactionLog log : logs) {
      if (log.getCreatedAt() == null) {
        continue;
      }
      LocalDate day = log.getCreatedAt().toLocalDate();
      String filename = log.getFilename();
      String user = log.getUserName();

      if (filename == null || user == null || user.isBlank()) {
        continue;
      }

      agg
        .computeIfAbsent(day, d -> new HashMap<>())
        .computeIfAbsent(filename, f -> new HashSet<>())
        .add(user);
    }

    // どれか 1 つの画像についてでも、ユーザ数が 2 人以上ならその日をハイライト
    Set<LocalDate> result = new HashSet<>();
    for (Map.Entry<LocalDate, Map<String, Set<String>>> e : agg.entrySet()) {
      boolean highlight = e.getValue().values().stream()
          .anyMatch(userSet -> userSet.size() >= 2);
      if (highlight) {
        result.add(e.getKey());
      }
    }

    return result;
  }

  // おまけ: 単一日付判定が必要なとき用
  public boolean isHighlightDate(LocalDate date) {
    YearMonth ym = YearMonth.from(date);
    return getHighlightDatesForMonth(ym).contains(date);
  }
}
