package com.alamin.ecommerce.subscribe;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<Subscribe, String> {
    Optional<Subscribe> findByEmail(String email);
}
