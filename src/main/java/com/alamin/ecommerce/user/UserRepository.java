package com.alamin.ecommerce.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query(value = "SELECT YEAR(created) FROM tbl_users GROUP BY YEAR(created) ORDER BY YEAR(created) DESC Limit 10", nativeQuery = true)
    List<Integer> getUsersRegisrationLabelsYear();

    @Query(value = "SELECT COUNT(created) FROM tbl_users GROUP BY YEAR(created) ORDER BY YEAR(created) DESC Limit 10", nativeQuery = true)
    List<Integer> getUsersRegisrationCountYear();

    @Query(value = "SELECT MONTH(created) FROM tbl_users WHERE YEAR(created) = :year GROUP BY MONTH(created) ORDER BY MONTH(created) ASC", nativeQuery = true)
    List<Integer> getUsersRegisrationLabelsMonth(@Param("year") int year);

    @Query(value = "SELECT MONTH(created) FROM tbl_users WHERE YEAR(created) = YEAR(NOW()) GROUP BY MONTH(created) ORDER BY MONTH(created) ASC", nativeQuery = true)
    List<Integer> getUsersRegisrationLabelsMonth();

    @Query(value = "SELECT COUNT(created) FROM tbl_users WHERE YEAR(created) = :year GROUP BY MONTH(created) ORDER BY MONTH(created) ASC", nativeQuery = true)
    List<Integer> getUsersRegisrationCountMonth(@Param("year") int year);

    @Query(value = "SELECT COUNT(created) FROM tbl_users WHERE YEAR(created) = YEAR(NOW()) GROUP BY MONTH(created) ORDER BY MONTH(created) ASC", nativeQuery = true)
    List<Integer> getUsersRegisrationCountMonth();
    
    @Query(value = "SELECT COUNT(u) FROM User u WHERE u.enabled = TRUE AND MONTH(u.created) = :month AND YEAR(u.created) = :year")
    List<Integer> getUsersByMonthAndYear(@Param("month") int month, @Param("year") int year);
}
