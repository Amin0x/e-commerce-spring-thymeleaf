package com.alamin.ecommerce.frontend.dto;

import java.time.LocalDateTime;

/**
 * Mirrored copy of the backend {@link com.alamin.ecommerce.statistics.Statistics}
 * entity JSON shape.
 */
public class StatisticsDto {

    private Long id;
    private LocalDateTime stDate;
    private Integer stValue;
    private String type;

    public StatisticsDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStDate() {
        return stDate;
    }

    public void setStDate(LocalDateTime stDate) {
        this.stDate = stDate;
    }

    public Integer getStValue() {
        return stValue;
    }

    public void setStValue(Integer stValue) {
        this.stValue = stValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
