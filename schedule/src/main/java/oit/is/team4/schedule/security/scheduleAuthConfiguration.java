package oit.is.team4.schedule.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class scheduleAuthConfiguration {
  /**
   * 認可処理に関する設定（認証されたユーザがどこにアクセスできるか）
   *
   * @param http
   * @return
   * @throws Exception
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.formLogin(login -> login
        .permitAll())
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")) // ログアウト後に / にリダイレクト
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/sample1/**").authenticated()
            .anyRequest().permitAll()) // 上記以外は全員アクセス可能
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/h2-console/*", "/sample2*/**","/addplan","/calendar")) // sample2用にCSRF対策を無効化
        .headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions
                .sameOrigin()));
    return http.build();
  }

  /**
   * 認証処理に関する設定（誰がどのようなロールでログインできるか）
   *
   * @return
   */
  @Bean
  public InMemoryUserDetailsManager userDetailsService() {

    // ユーザ名，パスワード，ロールを指定してbuildする
    // このときパスワードはBCryptでハッシュ化されているため，{bcrypt}とつける
    // ハッシュ化せずに平文でパスワードを指定する場合は{noop}をつける

    UserDetails user1 = User.withUsername("user1")
        .password("{bcrypt}$2y$10$ngxCDmuVK1TaGchiYQfJ1OAKkd64IH6skGsNw1sLabrTICOHPxC0e").roles("USER").build();
    UserDetails megane = User.withUsername("めがね")
        .password("{bcrypt}$2y$05$OowOzVe.CsNR.L.WPv6f6unABMUnQ48YqJEJJvF0x388Ic9udE9jO").roles("USER").build();
    UserDetails yani = User.withUsername("やに")
        .password("{bcrypt}$2y$05$SO1SWJAZKy5SBEDvx6sadOFE/RbEgdf9EHvGfZStJbSCnPI4TJEyq").roles("USER").build();
    UserDetails macho = User.withUsername("まっちょ")
        .password("{bcrypt}$2y$05$1brl0ftN2Xq1WseBi0oB6eCYqkFUQl.fJR7U62XR83dN4iwuwQOsK").roles("USER").build();
    UserDetails boshi = User.withUsername("ぼうし")
        .password("{bcrypt}$2y$05$VvJBwPnWKxMXgy83HPPPLOCsvB.0Ljkr.OUngmxxx6f4h/svEzuAy").roles("USER").build();
    UserDetails admin = User.withUsername("admin")
        .password("{bcrypt}$2y$10$ngxCDmuVK1TaGchiYQfJ1OAKkd64IH6skGsNw1sLabrTICOHPxC0e").roles("ADMIN").build();

    // 生成したユーザをImMemoryUserDetailsManagerに渡す（いくつでも良い）
    return new InMemoryUserDetailsManager(user1, megane, yani, macho, boshi, admin);
  }

}
