package oit.is.team4.schedule.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "image_likes")
public class ImageLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String filename;

  private int heartCount;
  private int likeCount;
  private int laughCount;

  public ImageLike() {
  }

  public ImageLike(String filename, int heartCount, int likeCount, int laughCount) {
    this.filename = filename;
    this.heartCount = heartCount;
    this.likeCount = likeCount;
    this.laughCount = laughCount;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public int getHeartCount() {
    return heartCount;
  }

  public void setHeartCount(int heartCount) {
    this.heartCount = heartCount;
  }

  public int getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(int likeCount) {
    this.likeCount = likeCount;
  }

  public int getLaughCount() {
    return laughCount;
  }

  public void setLaughCount(int laughCount) {
    this.laughCount = laughCount;
  }

  public void increment(String type) {
    if ("heart".equals(type)) {
      this.heartCount++;
    } else if ("like".equals(type)) {
      this.likeCount++;
    } else if ("laugh".equals(type)) {
      this.laughCount++;
    }
  }

  public void decrement(String type) {
    if ("heart".equals(type)) {
      this.heartCount--;
    } else if ("like".equals(type)) {
      this.likeCount--;
    } else if ("laugh".equals(type)) {
      this.laughCount--;
    }
  }
}
