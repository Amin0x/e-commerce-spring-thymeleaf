package com.alamin.ecommerce.user;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tbl_state")
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int stateId;
    private String name;
    private String nameAr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;
    
    @OneToMany(mappedBy = "state", fetch = FetchType.LAZY)
    private List<City> cities;

    public State() {
    }

    public State(int id, String name, Country country) {
        this.stateId = id;
        this.name = name;
        this.country = country;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }
}