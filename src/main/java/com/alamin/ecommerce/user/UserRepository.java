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
    int getCreatedUsersThisMonthCount();

    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = TRUE")
    int getEnabledUsersCount();

    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = FALSE")
    int getDisabledUsersCount();

    @Query("SELECT u FROM User u ORDER BY u.created DESC")
    List<User> getLastCreatedUsers(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.username = :name")
    Optional<User> getUserByUsername(@Param("name") String name);

    @Query("SELECT u FROM User u WHERE u.uuid = :uuid")
    Optional<User> getUserByUuid(@Param("uuid") String uuid);

    @Query(value = "SELECT YEAR(created) FROM tbl_users GROUP BY YEAR(created) ORDER BY YEAR(created) DESC Limit 10", nativeQuery = true)
    List<Integer> getUsersRegistrationLabelsYear();

    @Query(value = "SELECT COUNT(created) FROM tbl_users GROUP BY YEAR(created) ORDER BY YEAR(created) DESC Limit 10", nativeQuery = true)
    List<Integer> getUsersRegistrationCountYear();

    @Query(value = "SELECT MONTH(created) FROM tbl_users WHERE YEAR(created) = :year GROUP BY MONTH(created) ORDER BY MONTH(created) ASC", nativeQuery = true)
    List<Integer> getUsersRegistrationLabelsMonth(@Param("year") int year);

    @Query(value = "SELECT MONTH(created) FROM tbl_users WHERE YEAR(created) = YEAR(NOW()) GROUP BY MONTH(created) ORDER BY MONTH(created) ASC", nativeQuery = true)
    List<Integer> getUsersRegistrationLabelsMonth();

    @Query(value = "SELECT COUNT(created) FROM tbl_users WHERE YEAR(created) = :year GROUP BY MONTH(created) ORDER BY MONTH(created) ASC", nativeQuery = true)
    List<Integer> getUsersRegistrationCountMonth(@Param("year") int year);

    @Query(value = "SELECT COUNT(created) FROM tbl_users WHERE YEAR(created) = YEAR(NOW()) GROUP BY MONTH(created) ORDER BY MONTH(created) ASC", nativeQuery = true)
    List<Integer> getUsersRegistrationCountMonth();
    
    @Query(value = "SELECT COUNT(u) FROM User u WHERE u.enabled = TRUE AND MONTH(u.created) = :month AND YEAR(u.created) = :year")
    List<Integer> getUsersByMonthAndYear(@Param("month") int month, @Param("year") int year);
}
