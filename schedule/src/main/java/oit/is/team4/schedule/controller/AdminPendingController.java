package oit.is.team4.schedule.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import oit.is.team4.schedule.model.PendingUser;
import oit.is.team4.schedule.repository.PendingUserRepository;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminPendingController {

  private final PendingUserRepository pendingRepo;
  private final InMemoryUserDetailsManager userDetailsManager;

  public AdminPendingController(PendingUserRepository pendingRepo, InMemoryUserDetailsManager userDetailsManager) {
    this.pendingRepo = pendingRepo;
    this.userDetailsManager = userDetailsManager;
  }

  @GetMapping("/pending")
  public String listPending(Model model) {
    List<PendingUser> items = pendingRepo.findAll();
    model.addAttribute("pendings", items);
    return "admin/pending";
  }

  @GetMapping("/pending/api")
  @ResponseBody
  public List<Map<String, Object>> pendingApi() {
    List<PendingUser> items = pendingRepo.findAll();
    return items.stream().map(p -> {
      Map<String, Object> m = new java.util.HashMap<>();
      m.put("id", p.getId());
      m.put("username", p.getUsername());
      m.put("createdAt", p.getCreatedAt() == null ? "" : p.getCreatedAt().toString());
      return m;
    }).collect(Collectors.toList());
  }

  @PostMapping("/pending/approve/{id}")
  public String approve(@PathVariable Long id, RedirectAttributes ra) {
    Optional<PendingUser> opt = pendingRepo.findById(id);
    if (opt.isEmpty()) {
      ra.addFlashAttribute("error", "申請が見つかりません。");
      return "redirect:/admin/pending";
    }
    PendingUser pu = opt.get();
    if (userDetailsManager.userExists(pu.getUsername())) {
      pendingRepo.delete(pu);
      ra.addFlashAttribute("message", "ユーザは既に存在していたため申請を削除しました。");
      return "redirect:/admin/pending";
    }
    var user = User.withUsername(pu.getUsername())
        .password(pu.getPassword()) // エンコード済みをそのまま設定
        .roles("USER")
        .build();
    userDetailsManager.createUser(user);
    pendingRepo.delete(pu);
    ra.addFlashAttribute("message", "ユーザを承認しました。");
    return "redirect:/admin/pending";
  }

  @PostMapping("/pending/reject/{id}")
  public String reject(@PathVariable Long id, RedirectAttributes ra) {
    if (pendingRepo.existsById(id)) {
      pendingRepo.deleteById(id);
      ra.addFlashAttribute("message", "申請を却下しました。");
    } else {
      ra.addFlashAttribute("error", "申請が見つかりません。");
    }
    return "redirect:/admin/pending";
  }
}
