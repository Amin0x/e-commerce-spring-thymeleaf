package com.alamin.ecommerce.user;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_cities")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String nameAr;
    private String estimatedDelivery;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private State state;

    public City() {
    }

    public City(Integer id, String name, String nameAr, State state) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.nameAr = nameAr;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(String estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }
}