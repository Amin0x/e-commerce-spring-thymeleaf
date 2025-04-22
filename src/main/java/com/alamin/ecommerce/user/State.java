package com.alamin.ecommerce.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tbl_state")
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String nameAr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @JsonIgnore
    @OneToMany(mappedBy = "state", fetch = FetchType.LAZY)
    private List<City> cities = new ArrayList<>();

    public State() {
    }

    public State(String name, String nameAr, Country country) {
        this.name = name;
        this.nameAr = nameAr;
        this.country = country;
    }
}