package oit.is.team4.schedule.controller;

import java.security.Principal;
import java.util.Optional;
import java.net.URLEncoder; // 追加
import java.nio.charset.StandardCharsets; // 追加

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import oit.is.team4.schedule.model.ImageLike;
import oit.is.team4.schedule.model.ImageReactionLog;
import oit.is.team4.schedule.repository.ImageLikeRepository;
import oit.is.team4.schedule.repository.ImageReactionLogRepository;

@Controller
public class LikeController {

  private final ImageLikeRepository imageLikeRepository;
  private final ImageReactionLogRepository imageReactionLogRepository;

  public LikeController(ImageLikeRepository imageLikeRepository,
      ImageReactionLogRepository imageReactionLogRepository) {
    this.imageLikeRepository = imageLikeRepository;
    this.imageReactionLogRepository = imageReactionLogRepository;
  }

  @PostMapping("/sampleimage/react")
  public String reactSampleImage(@RequestParam String filename,
      @RequestParam String type,
      Principal principal,
      RedirectAttributes ra) {

    String f = (filename == null || filename.isBlank()) ? "sample.png" : filename;
    String username = (principal != null) ? principal.getName() : "匿名";

    try {
      // 1. 全体の集計データを取得（なければ作成）
      ImageLike like = imageLikeRepository.findByFilename(f)
          .orElseGet(() -> new ImageLike(f, 0, 0, 0));

      // 2. このユーザの過去のリアクション履歴を取得
      Optional<ImageReactionLog> logOpt = imageReactionLogRepository.findByUserNameAndFilename(username, f);

      // 3. ロジック分岐
      if (logOpt.isPresent()) {
        ImageReactionLog existingLog = logOpt.get();

        if (existingLog.getReactionType().equals(type)) {
          // A. [トグル] 取り消し
          like.decrement(existingLog.getReactionType());
          imageReactionLogRepository.delete(existingLog);
        } else {
          // B. [変更] 乗り換え
          like.decrement(existingLog.getReactionType());
          like.increment(type);
          existingLog.setReactionType(type);
          imageReactionLogRepository.save(existingLog);
        }
      } else {
        // C. [新規] 追加
        like.increment(type);
        ImageReactionLog newLog = new ImageReactionLog(username, f, type);
        imageReactionLogRepository.save(newLog);
      }

      // 集計データの保存
      imageLikeRepository.save(like);

    } catch (Exception e) {
      e.printStackTrace();
      ra.addFlashAttribute("error", "エラーが発生しました: " + e.getMessage());
    }

    // ★修正箇所: 日本語ファイル名を含むURLをエンコードする
    // これを行わないと "The Unicode character [例] ... cannot be encoded" エラーになります
    String encodedFilename = URLEncoder.encode(f, StandardCharsets.UTF_8);
    return "redirect:/sampleimage?filename=" + encodedFilename;
  }
}
