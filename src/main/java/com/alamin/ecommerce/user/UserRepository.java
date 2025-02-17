package com.alamin.ecommerce.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT COUNT(u) FROM User u")
    int getUsersCount();

    @Query("SELECT COUNT(u) FROM User u WHERE FUNCTION('MONTH', u.created) = FUNCTION('MONTH', NOW())")
    int getUsersCountThisMonth();

    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = TRUE")
    int getActiveUsersCount();

    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = FALSE")
    int getUnactiveUsersCount();

    @Query("SELECT u FROM User u ORDER BY u.created DESC")
    List<User> getLastRegisteredUsers(Pageable pageable);

    Optional<User> findByUsername(String name);

    Optional<User> findByUuid(String id);
}
