package com.alamin.ecommerce.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tbl_countries")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(message = "Country name is required")
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @NotBlank(message = "Country name in Arabic is required")
    @Column(name = "name_ar", nullable = false, unique = true)
    private String nameAr;
    private Boolean enabled;
    @JsonIgnore
    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    private List<State> states = new ArrayList<>();

}


