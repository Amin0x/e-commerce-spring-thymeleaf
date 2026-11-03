package com.alamin.ecommerce.frontend.dto;

import java.util.List;

/**
 * Mirrored copy of the backend {@link com.alamin.ecommerce.user.Country}
 * entity JSON shape.
 */
public class Country {

    private Integer id;
    private String name;
    private String nameAr;
    private Boolean enabled;

    public Country() {
    }

    public Country(Integer id, String name, String nameAr, Boolean enabled) {
        this.id = id;
        this.name = name;
        this.nameAr = nameAr;
        this.enabled = enabled;
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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
