package oit.is.team4.schedule.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
    return "upload"; // upload.html を表示
  }

  @PostMapping("/upload")
  public String upload(@RequestParam("imageFile") MultipartFile file) { // answerを削除

    if (file.isEmpty()) {
      return "redirect:/upload?error";
    }

    try {
      // --- ステップ A: 物理ファイルの保存 ---
      String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
      Path path = Paths.get(UPLOAD_DIR + fileName);

      if (!Files.exists(path.getParent())) {
        Files.createDirectories(path.getParent());
      }

      Files.copy(file.getInputStream(), path);

      // --- ステップ B: DBへのINSERT ---
      // ファイル名だけを保存するメソッドに変更
      imageMapper.insertImage(fileName);

    } catch (IOException e) {
      e.printStackTrace();
      return "redirect:/upload?error";
    }

    return "redirect:/upload?success"; // アップロード画面に戻る（あるいは一覧画面へ）
  }

  // 追加: DBからファイル名一覧を取得して一覧ページを表示する
  @GetMapping("/images")
  public String listImages(Model model) {
    List<String> names = imageMapper.selectAllImageNames();
    model.addAttribute("images", names);
    return "images"; // templates/images.html を表示
  }

}
