package oit.is.team4.schedule.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import oit.is.team4.schedule.mapper.ImageMapper;

@Controller
public class ImageController {
  private static final String UPLOAD_DIR = "uploads/";

  @Autowired
  private ImageMapper imageMapper;

  @GetMapping("/upload")
  public String showUploadForm() {
    return "upload";
  }

  @PostMapping("/upload")
  public String upload(@RequestParam("imageFile") MultipartFile file,
      // 修正: HTMLのdatetime-local形式に合わせてフォーマットを指定
      @RequestParam("scheduledTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime scheduledTime) {

    if (file.isEmpty()) {
      return "redirect:/upload?error";
    }

    try {
      String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
      Path path = Paths.get(UPLOAD_DIR + fileName);

      if (!Files.exists(path.getParent())) {
        Files.createDirectories(path.getParent());
      }

      Files.copy(file.getInputStream(), path);

      imageMapper.insertImage(fileName, scheduledTime);

    } catch (IOException e) {
      e.printStackTrace();
      return "redirect:/upload?error";
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
}
