package com.alamin.ecommerce.statistics;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Statistics {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long statisticId;
  private LocalDateTime stDate;
  private int stValue;
  private String type;

  public Statistics() {}

  public Statistics(int stValue, String type) {
    this.stValue = stValue;
    this.type = type;
    this.stDate = LocalDateTime.now();
  }

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
