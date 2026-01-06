package oit.is.team4.schedule.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.ZoneId;
import jakarta.persistence.Table;

@Entity
@Table(name = "reactionlog")
public class ImageReactionLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String userName;
  private String filename;
  private String reactionType;
  private LocalDateTime createdAt;

  public ImageReactionLog() {
  }

  public ImageReactionLog(String userName, String filename, String reactionType) {
    this.userName = userName;
    this.filename = filename;
    this.reactionType = reactionType;
    createdAt = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));
  }

  // GetterとSetter
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getReactionType() {
    return reactionType;
  }

  public void setReactionType(String reactionType) {
    this.reactionType = reactionType;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
