package oit.is.team4.schedule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.team4.schedule.security.ActiveUserService;

@Controller
public class Sample1Controller {

  private final ActiveUserService activeUserService;

  public Sample1Controller(ActiveUserService activeUserService) {
    this.activeUserService = activeUserService;
  }

  @GetMapping("/sample1")
  public String sample1(ModelMap model) {
    model.addAttribute("activeUsers", activeUserService.getActiveUsers());
    return "sample1";
  }

}
