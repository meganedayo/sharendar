package oit.is.team4.schedule.controller;

import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import oit.is.team4.schedule.security.ActiveUserService;

@RestController
public class ActiveUserApiController {

  private final ActiveUserService activeUserService;

  public ActiveUserApiController(ActiveUserService activeUserService) {
    this.activeUserService = activeUserService;
  }

  @GetMapping("/active-users")
  public Set<String> getActiveUsers() {
    return activeUserService.getActiveUsers();
  }
}
