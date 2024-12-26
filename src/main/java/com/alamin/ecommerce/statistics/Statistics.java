package com.alamin.ecommerce.statistics;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Statistics {
  @Id
  private Long statisticId;
  private LocalDateTime stDate;
  private String type;
  private int stValue;

  // Getter and Setter for id
  public Long getId() {
    return statisticId;
  }

  public void setId(Long id) {
    this.statisticId = id;
  }

  // Getter and Setter for dateVal
  public LocalDateTime getDateVal() {
    return stDate;
  }

  public void setDateVal(LocalDateTime dateVal) {
    this.stDate = dateVal;
  }

  // Getter and Setter for type
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  // Getter and Setter for value
  public int getValue() {
    return stValue;
  }

  public void setValue(int value) {
    this.stValue = value;
  }
}
