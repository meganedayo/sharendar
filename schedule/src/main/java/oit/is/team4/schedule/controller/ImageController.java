package oit.is.team4.schedule.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap; // 追加
import java.util.List;
import java.util.Map; // 追加
import java.util.UUID;
import java.util.stream.Collectors; // 追加

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody; // 追加
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import oit.is.team4.schedule.mapper.ImageMapper;
import oit.is.team4.schedule.model.ImageRecord; // 追加

@Controller
public class ImageController {
  private static final String UPLOAD_DIR = "uploads/";

  @Autowired
  private ImageMapper imageMapper;

  @GetMapping("/upload")
  public String showUploadForm(Model model) {
    return "upload";
  }

  @PostMapping("/upload")
  public String upload(@RequestParam("imageFile") MultipartFile file,
      @RequestParam("scheduledTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime scheduledTime,
      Principal principal,
      RedirectAttributes ra) {

    // 1. ファイル空チェック
    if (file.isEmpty()) {
      ra.addFlashAttribute("error", "ファイルを選択してください");
      return "redirect:/upload";
    }

    // 2. 未来の日時チェック (日本時間を基準にする)
    if (scheduledTime.isAfter(LocalDateTime.now(ZoneId.of("Asia/Tokyo")))) {
      ra.addFlashAttribute("error", "未来の日時は指定できません（現在までの日時を選択してください）");
      return "redirect:/calendar";
    }

    try {
      String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
      Path path = Paths.get(UPLOAD_DIR + fileName);

      if (!Files.exists(path.getParent())) {
        Files.createDirectories(path.getParent());
      }

      Files.copy(file.getInputStream(), path);

      String userName = (principal != null) ? principal.getName() : "匿名";
      imageMapper.insertImage(fileName, scheduledTime, userName);

    } catch (IOException e) {
      e.printStackTrace();
      ra.addFlashAttribute("error", "アップロード中にエラーが発生しました");
      return "redirect:/upload";
    }

    String dateParam = scheduledTime.toLocalDate().toString();

    return "redirect:/schedule/day?date=" + dateParam;
  }

  @GetMapping("/images")
  public String listImages(Model model) {
    List<String> names = imageMapper.selectAllImageNames();
    model.addAttribute("images", names);
    return "images";
  }

  @GetMapping("/schedule/by-date")
  public String imagesByDate(
      @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      Model model) {

    List<String> images = imageMapper.selectImageNamesByDate(date);
    model.addAttribute("images", images);
    model.addAttribute("date", date.toString());
    return "schedule_day";
  }

  // 【重要】非同期更新用API：指定日付の画像リストをJSONで返す
  // これがないと、JSからの問い合わせがエラーになり、画面が更新されません
  @GetMapping("/images/api/list")
  @ResponseBody
  public List<Map<String, Object>> getImageList(@RequestParam("date") String dateString) {
    LocalDate date = LocalDate.parse(dateString);
    LocalDateTime start = date.atStartOfDay();
    LocalDateTime end = date.plusDays(1).atStartOfDay();

    // その日の画像をDBから取得
    List<ImageRecord> records = imageMapper.selectImagesByDate(start, end);

    // JSON用に変換
    return records.stream().map(r -> {
      Map<String, Object> map = new HashMap<>();
      map.put("filename", r.getImageName());
      // 何時か(0-23)を計算して渡す
      map.put("hour", r.getScheduledTime().getHour());
      return map;
    }).collect(Collectors.toList());
  }
}
