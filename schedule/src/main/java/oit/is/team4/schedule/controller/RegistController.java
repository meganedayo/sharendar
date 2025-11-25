package oit.is.team4.schedule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegistController {
  @GetMapping("/registuser")
  public String newpage() {
    return "registuser.html";
  }
}
