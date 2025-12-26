package oit.is.team4.schedule.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Schedule {
  private int id;
  private LocalDate planDate;
  private LocalTime startTime;
  private LocalTime endTime;
  private String title;
  private String userName; // 【追加】

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public LocalDate getPlanDate() {
    return planDate;
  }

  public void setPlanDate(LocalDate planDate) {
    this.planDate = planDate;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalTime startTime) {
    this.startTime = startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalTime endTime) {
    this.endTime = endTime;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }
}
