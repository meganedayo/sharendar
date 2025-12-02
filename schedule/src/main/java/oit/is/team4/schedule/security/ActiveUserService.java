package oit.is.team4.schedule.security;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.stereotype.Service;

@Service
public class ActiveUserService {
  private final Set<String> activeUsers = new CopyOnWriteArraySet<>();

  public void addUser(String username) {
    if (username != null) {
      activeUsers.add(username);
    }
  }

  public void removeUser(String username) {
    if (username != null) {
      activeUsers.remove(username);
    }
  }

  public Set<String> getActiveUsers() {
    return Collections.unmodifiableSet(activeUsers);
  }
}
