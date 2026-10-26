package com.alamin.ecommerce.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {
    List<City> findByState(State state);
    List<City> findByName(String cityName);
    List<City> findByCountry(Country country);
    List<City> findByStateAndCountry(State state, Country country);
}