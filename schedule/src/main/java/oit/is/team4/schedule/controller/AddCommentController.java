package oit.is.team4.schedule.controller;

import java.security.Principal;
import java.util.HashMap; // 追加
import java.util.List;
import java.util.Map; // 追加
import java.util.Optional;
import java.util.stream.Collectors; // 追加

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody; // 追加
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import oit.is.team4.schedule.model.Comment;
import oit.is.team4.schedule.repository.CommentRepository;
import oit.is.team4.schedule.repository.ImageLikeRepository;
import oit.is.team4.schedule.model.ImageLike;
import oit.is.team4.schedule.repository.ImageReactionLogRepository;
import oit.is.team4.schedule.model.ImageEntity;
import oit.is.team4.schedule.repository.ImageRepository;

@Controller
public class AddCommentController {

  private final CommentRepository commentRepository;
  private final ImageLikeRepository imageLikeRepository;
  private final ImageReactionLogRepository imageReactionLogRepository;
  private final ImageRepository imageRepository;

  public AddCommentController(CommentRepository commentRepository,
      ImageLikeRepository imageLikeRepository,
      ImageReactionLogRepository imageReactionLogRepository,
      ImageRepository imageRepository) {
    this.commentRepository = commentRepository;
    this.imageLikeRepository = imageLikeRepository;
    this.imageReactionLogRepository = imageReactionLogRepository;
    this.imageRepository = imageRepository;
  }

  @GetMapping("/sampleimage")
  public String showSampleImage(@RequestParam(required = false) String filename,
      Model model,
      Principal principal) {

    if (filename == null || filename.isBlank()) {
      filename = "sample.png";
    }
    List<Comment> comments = commentRepository.findByFilenameOrderByCreatedAtDesc(filename);
    model.addAttribute("comments", comments);
    model.addAttribute("filename", filename);
    ImageLike like = imageLikeRepository.findByFilename(filename).orElse(null);
    int heartCount = (like == null) ? 0 : like.getHeartCount();
    int likeCount = (like == null) ? 0 : like.getLikeCount();
    int laughCount = (like == null) ? 0 : like.getLaughCount();

    model.addAttribute("heartCount", heartCount);
    model.addAttribute("likeCount", likeCount);
    model.addAttribute("laughCount", laughCount);
    String username = (principal == null) ? "匿名" : principal.getName();
    model.addAttribute("username", username);

    boolean hasReacted = imageReactionLogRepository.existsByUserNameAndFilename(username, filename);
    model.addAttribute("hasReacted", hasReacted);

    ImageEntity img = imageRepository.findByImageName(filename);
    boolean isOwner = false;

    if (img != null) {
      if (img.getUserName() != null && img.getUserName().equals(username)) {
        isOwner = true;
      }
    }
    model.addAttribute("isOwner", isOwner);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    boolean isAdmin = false;
    if (auth != null && auth.getAuthorities() != null) {
      isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
    model.addAttribute("isAdmin", isAdmin);

    return "sampleimage";
  }

  // 画像存在チェックAPI
  @GetMapping("/sampleimage/exists")
  @ResponseBody
  public boolean checkImageExists(@RequestParam String filename) {
    ImageEntity img = imageRepository.findByImageName(filename);
    return img != null;
  }

  // 【追加】コメントリスト取得API (非同期更新用)
  @GetMapping("/sampleimage/api/comments")
  @ResponseBody
  public List<Map<String, Object>> getCommentList(@RequestParam String filename, Principal principal) {
    if (filename == null || filename.isBlank())
      filename = "sample.png";
    List<Comment> comments = commentRepository.findByFilenameOrderByCreatedAtDesc(filename);

    String currentUserName = (principal != null) ? principal.getName() : "匿名";
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    boolean isAdmin = false;
    if (auth != null && auth.getAuthorities() != null) {
      isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
    final boolean finalIsAdmin = isAdmin;

    // JSONで扱いやすい形式に変換して返す
    return comments.stream().map(c -> {
      Map<String, Object> map = new HashMap<>();
      map.put("id", c.getId());
      map.put("author", c.getAuthor());
      map.put("text", c.getText());
      map.put("createdAt", c.getCreatedAt().toString());
      // 削除権限があるかどうかを判定して渡す
      boolean canDelete = currentUserName.equals(c.getAuthor()) || finalIsAdmin;
      map.put("canDelete", canDelete);
      return map;
    }).collect(Collectors.toList());
  }

  @PostMapping("/sampleimage/comment")
  public String postComment(Principal principal,
      @RequestParam String filename,
      @RequestParam String text,
      RedirectAttributes ra) {

    if (filename == null || filename.isBlank())
      filename = "sample.png";

    if (text == null || text.isBlank()) {
      ra.addFlashAttribute("error", "コメントを入力してください。");
      return "redirect:/sampleimage?filename=" + filename;
    }

    Comment c = new Comment();
    c.setFilename(filename);
    String author = (principal == null) ? "匿名" : principal.getName();
    c.setAuthor(author);
    c.setText(text);
    commentRepository.save(c);

    String enc = URLEncoder.encode(filename, StandardCharsets.UTF_8);
    return "redirect:/sampleimage?filename=" + enc;
  }

  @PostMapping("/sampleimage/delete")
  public String deleteImage(@RequestParam String filename, Principal principal, RedirectAttributes ra) {

    if (filename == null || filename.isBlank()) {
      return "redirect:/calendar";
    }

    String currentUserName = (principal != null) ? principal.getName() : "匿名";
    ImageEntity img = imageRepository.findByImageName(filename);

    if (img == null || !img.getUserName().equals(currentUserName)) {
      ra.addFlashAttribute("error", "削除権限がありません。");
      return "redirect:/sampleimage?filename=" + filename;
    }

    commentRepository.deleteByFilename(filename);
    imageLikeRepository.deleteByFilename(filename);
    imageReactionLogRepository.deleteByFilename(filename);
    imageRepository.delete(img);

    try {
      Path path = Paths.get("uploads/" + filename);
      if (Files.exists(path)) {
        Files.delete(path);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    ra.addFlashAttribute("message", "画像を削除しました。");
    return "redirect:/calendar";
  }

  @PostMapping("/sampleimage/comment/delete")
  public String deleteComment(Authentication authentication, @RequestParam Long id, RedirectAttributes ra) {
    Optional<Comment> opt = commentRepository.findById(id);
    if (opt.isEmpty()) {
      ra.addFlashAttribute("error", "コメントが見つかりません。");
      return "redirect:/sampleimage";
    }

    Comment c = opt.get();
    String currentUser = (authentication == null) ? "匿名" : authentication.getName();

    boolean isAdmin = false;
    if (authentication != null && authentication.getAuthorities() != null) {
      isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    if (currentUser.equals(c.getAuthor()) || isAdmin) {
      commentRepository.delete(c);
      ra.addFlashAttribute("message", "コメントを削除しました。");
    } else {
      ra.addFlashAttribute("error", "コメントの削除権限がありません。");
    }

    String encFilename = URLEncoder.encode(c.getFilename(), StandardCharsets.UTF_8);
    return "redirect:/sampleimage?filename=" + encFilename;
  }
}
