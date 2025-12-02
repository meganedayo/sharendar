package oit.is.team4.schedule.security;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {

  private final ActiveUserService activeUserService;

  public SessionListener(ActiveUserService activeUserService) {
    this.activeUserService = activeUserService;
  }

  @Override
  public void sessionCreated(HttpSessionEvent se) {
    // 何もしない
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent se) {
    var session = se.getSession();
    Object u = session.getAttribute("ACTIVE_USER");
    if (u != null) {
      activeUserService.removeUser(u.toString());
    }
  }
}
