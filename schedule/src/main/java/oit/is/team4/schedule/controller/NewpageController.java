package oit.is.team4.schedule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NewpageController {

  @GetMapping("/newpage")
  public String newpage() {
    return "newpage.html";
  }
}
