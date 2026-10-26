package com.alamin.ecommerce.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Integer> {
    Optional<State> findByName(String name);
    List<State> findByCountry(Country country);
}
