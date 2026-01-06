package oit.is.team4.schedule.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashSet;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import oit.is.team4.schedule.mapper.ImageMapper;
import oit.is.team4.schedule.repository.ImageReactionLogRepository;

/**
 * reactionlog を元に、
 * 同じ日付に同じ画像へ 2 人以上のユーザがリアクションした日をハイライト対象とするサービス。
 */
@Service
public class ReactionHighlightService {

  private final ImageReactionLogRepository imageReactionLogRepository;
  private final ImageMapper imageMapper;

  public ReactionHighlightService(ImageReactionLogRepository imageReactionLogRepository, ImageMapper imageMapper) {
    this.imageReactionLogRepository = imageReactionLogRepository;
    this.imageMapper = imageMapper;
  }

  /**
   * 指定年月の中で、ハイライトすべき LocalDate の集合を返す。
   */
  public Set<LocalDate> getHighlightDatesForMonth(YearMonth yearMonth) {
    LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));
    LocalDateTime windowStart = now.minusHours(24);

    // 2. 直近24時間で「同一画像に distinct user が2人以上」リアクションした画像名を取得
    List<String> hotImageNames = imageReactionLogRepository.findHotFilenames(windowStart, now, 2);

    if (hotImageNames == null || hotImageNames.isEmpty()) {
      return Set.of();
    }

    // 3. image テーブルから scheduled_time を取得
    // ここは ImageMapper 側に selectScheduledTimesByNames(...) を追加する（下で説明）
    var images = imageMapper.selectImagesByNames(hotImageNames);

    // 4. scheduled_time の日付をハイライト対象にする（表示中 month のみ）
    Set<LocalDate> result = new HashSet<>();
    for (var img : images) {
      if (img == null || img.getScheduledTime() == null)
        continue;

      LocalDate scheduledDate = img.getScheduledTime().toLocalDate();
      if (YearMonth.from(scheduledDate).equals(yearMonth)) {
        result.add(scheduledDate);
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
