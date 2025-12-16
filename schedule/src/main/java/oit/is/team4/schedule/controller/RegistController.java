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
import oit.is.team4.schedule.model.PendingUser;
import oit.is.team4.schedule.repository.PendingUserRepository;

@Controller
public class RegistController {

  private final PendingUserRepository pendingUserRepository;

  private final InMemoryUserDetailsManager userDetailsManager;
  private final PasswordEncoder passwordEncoder;

  public RegistController(InMemoryUserDetailsManager userDetailsManager,
      PasswordEncoder passwordEncoder, PendingUserRepository pendingUserRepository) {
    this.userDetailsManager = userDetailsManager;
    this.passwordEncoder = passwordEncoder;
    this.pendingUserRepository = pendingUserRepository;
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

    if (userDetailsManager.userExists(username) || pendingUserRepository.findByUsername(username).isPresent()) {
      model.addAttribute("error", "そのユーザ名は既に使われているか申請中です。");
      return "registuser";
    }

    PendingUser pu = new PendingUser();
    pu.setUsername(username);
    pu.setPassword(passwordEncoder.encode(password));
    pendingUserRepository.save(pu);

    ra.addFlashAttribute("message", "登録申請を受け付けました。管理者の承認をお待ちください。");
    return "redirect:/auth/registuser";
  }
}
