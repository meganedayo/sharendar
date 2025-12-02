package oit.is.team4.schedule.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // file:uploads/ はアプリ起動ディレクトリの uploads フォルダを指します
    registry.addResourceHandler("/uploads/**")
        .addResourceLocations("file:uploads/");
  }
}
