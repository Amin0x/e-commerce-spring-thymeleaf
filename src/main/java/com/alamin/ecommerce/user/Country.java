package com.alamin.ecommerce.user;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "tbl_countries")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int countryId;
    private String name;
    private String nameAr;
    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    private List<State> states;

    public Country() {
    }

    public Country(String name) {
        this.name = name;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }
}


