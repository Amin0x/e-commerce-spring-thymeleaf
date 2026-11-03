package com.alamin.ecommerce.frontend.dto;

/**
 * Mirrored copy of the backend {@link com.alamin.ecommerce.user.City}
 * entity JSON shape.
 */
public class City {

    private Integer id;
    private String name;
    private String nameAr;
    private int estimatedDelivery;
    private String estimatedDeliveryUnit;
    private String deliveryPriority;
    private double deliveryPrice;
    private String status;

    public City() {
    }

    public City(Integer id, String name, String nameAr, int estimatedDelivery,
                String estimatedDeliveryUnit, String deliveryPriority, double deliveryPrice, String status) {
        this.id = id;
        this.name = name;
        this.nameAr = nameAr;
        this.estimatedDelivery = estimatedDelivery;
        this.estimatedDeliveryUnit = estimatedDeliveryUnit;
        this.deliveryPriority = deliveryPriority;
        this.deliveryPrice = deliveryPrice;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public int getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(int estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public String getEstimatedDeliveryUnit() {
        return estimatedDeliveryUnit;
    }

    public void setEstimatedDeliveryUnit(String estimatedDeliveryUnit) {
        this.estimatedDeliveryUnit = estimatedDeliveryUnit;
    }

    public String getDeliveryPriority() {
        return deliveryPriority;
    }

    public void setDeliveryPriority(String deliveryPriority) {
        this.deliveryPriority = deliveryPriority;
    }

    public double getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
