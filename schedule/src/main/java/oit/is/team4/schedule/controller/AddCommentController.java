package oit.is.team4.schedule.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import oit.is.team4.schedule.model.Comment;
import oit.is.team4.schedule.repository.CommentRepository;

@Controller
public class AddCommentController {

  private final CommentRepository commentRepository;

  public AddCommentController(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  @GetMapping("/sampleimage")
  public String showSampleImage(Model model) {
    List<Comment> comments = commentRepository.findByFilenameOrderByCreatedAtDesc("sample.png");
    model.addAttribute("comments", comments);
    model.addAttribute("filename", "sample.png");
    return "sampleimage";
  }

  @PostMapping("/sampleimage/comment")
  public String postComment(@RequestParam(required = false) String author,
      @RequestParam String text,
      RedirectAttributes ra) {
    if (text == null || text.isBlank()) {
      ra.addFlashAttribute("error", "コメントを入力してください。");
      return "redirect:/sampleimage";
    }

    Comment c = new Comment();
    c.setFilename("sample.png");
    c.setAuthor((author == null || author.isBlank()) ? "匿名" : author);
    c.setText(text);
    commentRepository.save(c);

    return "redirect:/sampleimage";
  }
}
