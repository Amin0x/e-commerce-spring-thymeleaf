package com.alamin.ecommerce.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        final List<User> users = userRepository.findAll();
        return users;
    }

    @Override
    public Page<User> getAllUsers(int page, int size) {
        final Page<User> users = userRepository.findAll(PageRequest.of(page, size));
        return users;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        if (id == null)
            throw new IllegalArgumentException("User ID cannot be null");

        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUUID(String id) {
        if (id == null)
            throw new IllegalArgumentException("User UUID cannot be null");

        return userRepository.getUserByUuid(id);
    }

    @Override
    public int getUsersCountThisMonth() {
        int usersThisMonth = 0;

        try {
            usersThisMonth = userRepository.getCreatedUsersThisMonthCount();

        } catch (Exception e) {
            //throw new RuntimeException(e);
        }

        return usersThisMonth;
    }

    @Override
    public List<User> getLastUsers(int size) {
        if (size <= 0)
            throw new IllegalArgumentException("Size must be greater than 0");
        if (size > 100)
            throw new IllegalArgumentException("Size must be less than or equal to 100");

        return userRepository.getLastCreatedUsers(Pageable.ofSize(size));
    }

    @Override
    public long getUsersCount(){
        return userRepository.count();
    }

    @Override
    public Optional<User> getUserByName(String name) {
        if (name == null)
            throw new IllegalArgumentException("User name cannot be null");
        if (name.isEmpty())
            throw new IllegalArgumentException("User name cannot be empty");

        return userRepository.getUserByUsername(name);
    }

    @Override
    public User createUser(User user) {
        validateUser(user);

        User createdUser = new User();
        createdUser.setFirstName(user.getFirstName());
        createdUser.setLastName(user.getLastName());
        createdUser.setUsername(user.getUsername());
        createdUser.setPassword(user.getPassword());
        createdUser.setEmail(user.getEmail());
        createdUser.setStatus("ACTIVE");
        createdUser.setEnabled(true);
        createdUser.setBirthDate(user.getBirthDate());
        createdUser.setUuid(this.createUuid());
        return userRepository.save(createdUser);
    }

    private static void validateUser(User user) {
        if (user == null)
            throw new IllegalArgumentException("User cannot be null");
        if (user.getFirstName() == null || user.getFirstName().isEmpty())
            throw new IllegalArgumentException("User first name cannot be null or empty");
        if (user.getLastName() == null || user.getLastName().isEmpty())
            throw new IllegalArgumentException("User last name cannot be null or empty");
        if (user.getUsername() == null || user.getUsername().isEmpty())
            throw new IllegalArgumentException("User username cannot be null or empty");
        if (user.getPassword() == null || user.getPassword().isEmpty())
            throw new IllegalArgumentException("User password cannot be null or empty");
        if (user.getEmail() == null || user.getEmail().isEmpty())
            throw new IllegalArgumentException("User email cannot be null or empty");
        if (user.getBirthDate() == null)
            throw new IllegalArgumentException("User birth date cannot be null");
        if (user.getBirthDate().isAfter(java.time.LocalDate.now()))
            throw new IllegalArgumentException("User birth date cannot be in the future");
        if (user.getBirthDate().isBefore(java.time.LocalDate.of(1900, 1, 1)))
            throw new IllegalArgumentException("User birth date cannot be before 1900");
        if (user.getBirthDate().isAfter(java.time.LocalDate.now().minusYears(18)))
            throw new IllegalArgumentException("User must be at least 18 years old");
        if (user.getBirthDate().isBefore(java.time.LocalDate.now().minusYears(100)))
            throw new IllegalArgumentException("User birth date cannot be more than 100 years in the past");
    }

    @Override
    public User updateUser(Long id, User user) {
        User upatedUser = getUserById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        validateUser(user);
        upatedUser.setFirstName(user.getFirstName());
        upatedUser.setLastName(user.getLastName());
        upatedUser.setUsername(user.getUsername());
        upatedUser.setPassword(user.getPassword());
        upatedUser.setEmail(user.getEmail());
        upatedUser.setStatus("ACTIVE");
        upatedUser.setEnabled(true);
        upatedUser.setBirthDate(user.getBirthDate());
        return userRepository.save(upatedUser);
    }

    //
    private String createUuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public int getActiveUsersCount() {
       
        return userRepository.getEnabledUsersCount();
    }

    // This method is used to get the inactive users count
    @Override
    public int getInactiveUsersCount(){
        return userRepository.getDisabledUsersCount();
    }

    // This method is used to get the users registration count for the last 12 months
    @Override
    public List<Integer> getUsersRegistrationMonth() {
        List<Integer> usersRegisrationMonth = null;
        try {
            usersRegisrationMonth = userRepository.getUsersRegistrationCountMonth();
        } catch (Exception e) {
            //throw new RuntimeException(e);
        }
        return usersRegisrationMonth;
    }

    // This method is used to get the labels for the users registration chart
    @Override
    public List<Integer> getUsersRegistrationLabelsMonth() {
        List<Integer> usersRegisrationLabelsMonth = null;
        try {
            usersRegisrationLabelsMonth = userRepository.getUsersRegistrationLabelsMonth();
        } catch (Exception e) {
            //throw new RuntimeException(e);
        }
        return usersRegisrationLabelsMonth;
    }
}
