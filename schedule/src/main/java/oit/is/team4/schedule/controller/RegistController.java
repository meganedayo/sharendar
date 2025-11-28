package oit.is.team4.schedule.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class RegistController {

  private final InMemoryUserDetailsManager userDetailsManager;
  private final PasswordEncoder passwordEncoder;

  public RegistController(InMemoryUserDetailsManager userDetailsManager,
      PasswordEncoder passwordEncoder) {
    this.userDetailsManager = userDetailsManager;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping({ "/registuser", "/auth/registuser" })
  public String showForm() {
    return "registuser";
  }

  @PostMapping({ "/registuser", "/auth/registuser" })
  public String register(@RequestParam String username,
      @RequestParam String password,
      RedirectAttributes ra,
      Model model) {

    if (userDetailsManager.userExists(username)) {
      model.addAttribute("error", "そのユーザ名は既に使われています。");
      return "registuser";
    }

    var user = User.withUsername(username)
        .password(passwordEncoder.encode(password))
        .roles("USER")
        .build();
    userDetailsManager.createUser(user);

    ra.addFlashAttribute("message", "登録が完了しました。ログインしてください。");
    return "redirect:/auth/registuser";
  }
}
