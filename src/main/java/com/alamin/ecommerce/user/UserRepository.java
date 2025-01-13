package com.alamin.ecommerce.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT COUNT(u) FROM User u WHERE FUNCTION('MONTH', u.created) = FUNCTION('MONTH', NOW())")
    int getUsersThisMonth();
}
