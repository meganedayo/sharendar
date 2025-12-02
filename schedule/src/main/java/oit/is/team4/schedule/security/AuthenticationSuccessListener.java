package oit.is.team4.schedule.security;

import jakarta.servlet.http.HttpSession;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

  private final ActiveUserService activeUserService;

  public AuthenticationSuccessListener(ActiveUserService activeUserService) {
    this.activeUserService = activeUserService;
  }

  @Override
  public void onApplicationEvent(AuthenticationSuccessEvent event) {
    String username = event.getAuthentication().getName();

    // セッションにユーザ名を保存（後でセッション破棄時に取り除くため）
    var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attrs != null) {
      HttpSession session = attrs.getRequest().getSession(true);
      session.setAttribute("ACTIVE_USER", username);
    }

    activeUserService.addUser(username);
  }
}
