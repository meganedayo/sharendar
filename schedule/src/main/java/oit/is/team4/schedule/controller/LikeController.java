package oit.is.team4.schedule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import oit.is.team4.schedule.model.ImageLike;
import oit.is.team4.schedule.repository.ImageLikeRepository;

@Controller
public class LikeController {

  private final ImageLikeRepository imageLikeRepository;

  public LikeController(ImageLikeRepository imageLikeRepository) {
    this.imageLikeRepository = imageLikeRepository;
  }

  @PostMapping("/sampleimage/react")
  public String reactSampleImage(@RequestParam String type, RedirectAttributes ra) {
    String filename = "sample.png";
    ImageLike like = imageLikeRepository.findByFilename(filename)
        .orElseGet(() -> new ImageLike(filename, 0, 0, 0));
    like.increment(type);
    imageLikeRepository.save(like);
    return "redirect:/sampleimage";
  }
}
