package oit.is.team4.schedule.controller;

import java.security.Principal;

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
  public String reactSampleImage(@RequestParam String type, Principal principal, RedirectAttributes ra) {
    String filename = "sample.png";

    // 1. 既存の集計用テーブルの更新 (合計数を表示するため)
    ImageLike like = imageLikeRepository.findByFilename(filename)
        .orElseGet(() -> new ImageLike(filename, 0, 0, 0));
    like.increment(type);
    imageLikeRepository.save(like);

    // 2. ログの保存 (誰が押したかを記録するため)
    String username = (principal != null) ? principal.getName() : "匿名";
    ImageReactionLog log = new ImageReactionLog(username, filename, type);
    imageReactionLogRepository.save(log);

    return "redirect:/sampleimage";
  }
}
