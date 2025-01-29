package com.alamin.ecommerce.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT COUNT(u) FROM User u WHERE FUNCTION('MONTH', u.created) = FUNCTION('MONTH', NOW())")
    int getUsersThisMonth();

    @Query("SELECT u FROM User u ORDER BY u.created DESC")
    List<User> getLastUsers(Pageable pageable);

    //long count();

}
