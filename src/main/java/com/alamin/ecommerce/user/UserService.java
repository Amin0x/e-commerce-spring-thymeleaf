package com.alamin.ecommerce.user;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    Page<User> getAllUsers(int page, int size);

    Optional<User> getUserById(Long id);

    Optional<User> getUserByUUID(String id);

    int getUsersCountThisMonth();

    List<User> getLastUsers(int size);

    long getUsersCount();

    Optional<User> getUserByName(String name);

    User createUser(User user);

    User updateUser(Long id, User user);

    int getActiveUsersCount();

    int getInactiveUsersCount();

    List<Integer> getUsersRegistrationMonth();

    List<Integer> getUsersRegistrationLabelsMonth();
}
