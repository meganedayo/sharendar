package oit.is.team4.schedule.model;

import java.time.LocalDateTime;

public class ImageRecord {
  private String imageName;
  private LocalDateTime scheduledTime;

  public ImageRecord() {
  }

  public String getImageName() {
    return imageName;
  }

  public void setImageName(String imageName) {
    this.imageName = imageName;
  }

  public LocalDateTime getScheduledTime() {
    return scheduledTime;
  }

  public void setScheduledTime(LocalDateTime scheduledTime) {
    this.scheduledTime = scheduledTime;
  }
}
