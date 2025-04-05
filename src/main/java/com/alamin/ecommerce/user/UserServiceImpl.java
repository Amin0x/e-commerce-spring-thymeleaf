package com.alamin.ecommerce.user;

import org.springframework.beans.factory.annotation.Autowired;
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

        List<User> users = userRepository.findAll();
        return users;
    }

    @Override
    public Page<User> getAllUsers(int page, int size) {
        Page<User> users = userRepository.findAll(PageRequest.of(page, size));
        return users;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        if (id == null)
            return Optional.empty();

        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUUID(String id) {
        if (id == null)
            return Optional.empty();

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
        return userRepository.getLastCreatedUsers(Pageable.ofSize(size));
    }

    @Override
    public long getUsersCount(){
        return userRepository.count();
    }

    @Override
    public Optional<User> getUserByName(String name) {
        return userRepository.getUserByUsername(name);
    }

    @Override
    public User createUser(User user) {
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

    @Override
    public User updateUser(Long id, User user) {
        User upatedUser = getUserById(id).orElseThrow();
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
