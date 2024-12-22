import java.time.LocalDateTime;

@Entity
public class Statistics {
  private Long id;
  private LocalDateTime dateVal;
  private String type;
  private int value;

  // Getter and Setter for id
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  // Getter and Setter for dateVal
  public LocalDateTime getDateVal() {
    return dateVal;
  }

  public void setDateVal(LocalDateTime dateVal) {
    this.dateVal = dateVal;
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
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }
}
