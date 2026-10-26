package com.alamin.ecommerce.user;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
    Optional<Country> findByName(String countryName);
    Optional<Country> findByNameAr(String countryNameAr);
}