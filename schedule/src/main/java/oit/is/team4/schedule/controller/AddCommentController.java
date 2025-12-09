package oit.is.team4.schedule.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import oit.is.team4.schedule.model.Comment;
import oit.is.team4.schedule.repository.CommentRepository;
import oit.is.team4.schedule.repository.ImageLikeRepository;
import oit.is.team4.schedule.model.ImageLike;

@Controller
public class AddCommentController {

  private final CommentRepository commentRepository;
  private final ImageLikeRepository imageLikeRepository;

  public AddCommentController(CommentRepository commentRepository, ImageLikeRepository imageLikeRepository) {
    this.commentRepository = commentRepository;
    this.imageLikeRepository = imageLikeRepository;
  }

  @GetMapping("/sampleimage")
  public String showSampleImage(Model model, Principal principal) {
    List<Comment> comments = commentRepository.findByFilenameOrderByCreatedAtDesc("sample.png");
    model.addAttribute("comments", comments);
    model.addAttribute("filename", "sample.png");
    ImageLike like = imageLikeRepository.findByFilename("sample.png").orElse(null);
    int heartCount = (like == null) ? 0 : like.getHeartCount();
    int likeCount = (like == null) ? 0 : like.getLikeCount();
    int laughCount = (like == null) ? 0 : like.getLaughCount();

    model.addAttribute("heartCount", heartCount);
    model.addAttribute("likeCount", likeCount);
    model.addAttribute("laughCount", laughCount);
    model.addAttribute("username", principal == null ? "匿名" : principal.getName());
    return "sampleimage";
  }

  @PostMapping("/sampleimage/comment")
  public String postComment(Principal principal,
      @RequestParam String text,
      RedirectAttributes ra) { // { changed code } Principal から投稿者を取得
    if (text == null || text.isBlank()) {
      ra.addFlashAttribute("error", "コメントを入力してください。");
      return "redirect:/sampleimage";
    }

    Comment c = new Comment();
    c.setFilename("sample.png");
    String author = (principal == null) ? "匿名" : principal.getName(); // { changed code }
    c.setAuthor(author);
    c.setText(text);
    commentRepository.save(c);

    return "redirect:/sampleimage";
  }
}
