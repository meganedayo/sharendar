package oit.is.team4.schedule.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "image")
public class ImageEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String imageName;
  private String userName; // 投稿者
  private LocalDateTime scheduledTime; // 【重要】この行を追加

  public ImageEntity() {
  }

  public ImageEntity(String imageName, String userName, LocalDateTime scheduledTime) {
    this.imageName = imageName;
    this.userName = userName;
    this.scheduledTime = scheduledTime;
  }

  // Getter/Setter
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getImageName() {
    return imageName;
  }

  public void setImageName(String imageName) {
    this.imageName = imageName;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  // scheduledTime の Getter/Setter も追加
  public LocalDateTime getScheduledTime() {
    return scheduledTime;
  }

  public void setScheduledTime(LocalDateTime scheduledTime) {
    this.scheduledTime = scheduledTime;
  }
}
