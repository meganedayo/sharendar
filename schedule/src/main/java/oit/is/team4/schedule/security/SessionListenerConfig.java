package oit.is.team4.schedule.security;

import jakarta.servlet.http.HttpSessionListener;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionListenerConfig {

  @Bean
  public ServletListenerRegistrationBean<HttpSessionListener> sessionListener(ActiveUserService activeUserService) {
    ServletListenerRegistrationBean<HttpSessionListener> srb = new ServletListenerRegistrationBean<>(
        new SessionListener(activeUserService));
    srb.setOrder(Integer.MIN_VALUE);
    return srb;
  }
}
