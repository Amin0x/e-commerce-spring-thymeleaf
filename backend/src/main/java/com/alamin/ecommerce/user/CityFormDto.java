package com.alamin.ecommerce.user;

import lombok.Data;

@Data
public class CityFormDto {
    private Integer id;
    private String name;
    private String nameAr;
    private Integer estimatedDelivery;
    private String estimatedDeliveryUnit;
    private String deliveryPriority;
    private Double deliveryPrice;
    private String status;
    private Integer stateId;
    private Integer countryId;
}
