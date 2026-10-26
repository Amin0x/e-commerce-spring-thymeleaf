package com.alamin.ecommerce.statistics;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_statistics")
public class Statistics {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
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
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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
