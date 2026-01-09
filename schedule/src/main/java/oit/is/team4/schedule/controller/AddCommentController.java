package oit.is.team4.schedule.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody; // 追加
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import oit.is.team4.schedule.model.Comment;
import oit.is.team4.schedule.repository.CommentRepository;
import oit.is.team4.schedule.repository.ImageLikeRepository;
import oit.is.team4.schedule.model.ImageLike;
import oit.is.team4.schedule.repository.ImageReactionLogRepository;
import oit.is.team4.schedule.model.ImageEntity;
import oit.is.team4.schedule.repository.ImageRepository;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class AddCommentController {

  private final CommentRepository commentRepository;
  private final ImageLikeRepository imageLikeRepository;
  private final ImageReactionLogRepository imageReactionLogRepository;
  private final ImageRepository imageRepository;

  public AddCommentController(CommentRepository commentRepository,
      ImageLikeRepository imageLikeRepository,
      ImageReactionLogRepository imageReactionLogRepository,
      ImageRepository imageRepository) { // 追加
    this.commentRepository = commentRepository;
    this.imageLikeRepository = imageLikeRepository;
    this.imageReactionLogRepository = imageReactionLogRepository;
    this.imageRepository = imageRepository; // 追加
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

    // このユーザがこの画像に対してリアクション済みかどうかを調べる
    // (existsBy... メソッドは前回のステップでRepositoryに追加したものを使います)
    boolean hasReacted = imageReactionLogRepository.existsByUserNameAndFilename(username, filename);

    // 画面にフラグを渡す
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

  // 【追加】画像が存在するかチェックするAPI
  @GetMapping("/sampleimage/exists")
  @ResponseBody // HTMLではなくデータを返す場合に必要
  public boolean checkImageExists(@RequestParam String filename) {
    ImageEntity img = imageRepository.findByImageName(filename);
    // DBにデータがあれば true, なければ false を返す
    return img != null;
  }

  @PostMapping("/sampleimage/comment")
  public String postComment(Principal principal,
      @RequestParam String filename,
      @RequestParam String text,
      RedirectAttributes ra) { // { changed code } Principal から投稿者を取得

    if (filename == null || filename.isBlank()) {
      filename = "sample.png";
    }

    if (text == null || text.isBlank()) {
      ra.addFlashAttribute("error", "コメントを入力してください。");
      return "redirect:/sampleimage?filename=" + filename;
    }

    Comment c = new Comment();
    c.setFilename(filename);
    String author = (principal == null) ? "匿名" : principal.getName(); // { changed code }
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

    // 【追加】削除実行前の所有権チェック（セキュリティ対策）
    String currentUserName = (principal != null) ? principal.getName() : "匿名";
    ImageEntity img = imageRepository.findByImageName(filename);

    // 画像が存在しない、または所有者でない場合は削除させない
    if (img == null || !img.getUserName().equals(currentUserName)) {
      ra.addFlashAttribute("error", "削除権限がありません。");
      return "redirect:/sampleimage?filename=" + filename;
    }

    // 1. データベース削除
    commentRepository.deleteByFilename(filename);
    imageLikeRepository.deleteByFilename(filename);
    imageReactionLogRepository.deleteByFilename(filename);

    // 【追加】Imageテーブルからも削除
    imageRepository.delete(img);

    // 2. ファイル削除
    try {
      // ImageControllerと同じ "uploads/" ディレクトリを指定
      Path path = Paths.get("uploads/" + filename);

      // ファイルが存在すれば削除する
      if (Files.exists(path)) {
        Files.delete(path);
        System.out.println("ファイルを削除しました: " + path.toAbsolutePath());
      } else {
        System.out.println("ファイルが見つかりませんでした: " + path.toAbsolutePath());
      }
    } catch (Exception e) {
      e.printStackTrace();
      // ファイル削除に失敗しても、DBからは消えているので処理は続行（あるいはエラーメッセージを出す）
    }

    ra.addFlashAttribute("message", "画像を削除しました。");
    return "redirect:/calendar";
  }

  @PostMapping("/sampleimage/comment/delete")
  public String deleteComment(Authentication authentication, @RequestParam Long id, RedirectAttributes ra) { // {
                                                                                                             // changed
                                                                                                             // code }
    Optional<Comment> opt = commentRepository.findById(id);
    if (opt.isEmpty()) {
      ra.addFlashAttribute("error", "コメントが見つかりません。");
      return "redirect:/sampleimage"; // filename 不明時は一覧に戻す
    }

    Comment c = opt.get();
    String currentUser = (authentication == null) ? "匿名" : authentication.getName();

    boolean isAdmin = false;
    if (authentication != null && authentication.getAuthorities() != null) {
      isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")); // {
                                                                                                               // changed
                                                                                                               // code }
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
