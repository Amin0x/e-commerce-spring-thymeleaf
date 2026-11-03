package com.alamin.ecommerce.frontend.dto;

/**
 * Mirrored copy of the backend {@link com.alamin.ecommerce.user.State}
 * entity JSON shape.
 */
public class State {

    private Integer id;
    private String name;
    private String nameAr;
    private Country country;

    public State() {
    }

    public State(Integer id, String name, String nameAr, Country country) {
        this.id = id;
        this.name = name;
        this.nameAr = nameAr;
        this.country = country;
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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
