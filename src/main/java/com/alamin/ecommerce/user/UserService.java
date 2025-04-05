package com.alamin.ecommerce.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {

        List<User> users = userRepository.findAll();
        return users;
    }

    public Page<User> getAllUsers(int page, int size) {
        Page<User> users = userRepository.findAll(PageRequest.of(page, size));
        return users;
    }

    public Optional<User> getUserById(Long id) {
        if (id == null)
            return Optional.empty();

        return userRepository.findById(id);
    }

    public Optional<User> getUserByUUID(String id) {
        if (id == null)
            return Optional.empty();

        return userRepository.getUserByUuid(id);
    }

    public int getUsersCountThisMonth() {
        int usersThisMonth = 0;

        try {
            usersThisMonth = userRepository.getCreatedUsersThisMonthCount();

        } catch (Exception e) {
            //throw new RuntimeException(e);
        }

        return usersThisMonth;
    }

    private String saveFileFromJson(byte[] bytes, String fileName) throws IOException {
        Path path = null;
        String strPath = "uploads/" + fileName;

        // Check file size before saving
        if (bytes.length > 5 * 1024 * 1024) { // 5MB
            throw new IOException("File size exceeds the maximum limit of 5MB.");
        }
        
        try {
            // Save the file to a directory
            path = Paths.get(strPath);
            Files.write(path, bytes);
        } catch (IOException e) {
            log.error("Error saving file " + fileName, e);
            throw new IOException("Error saving file: " + fileName);
        }

        return strPath;
    }





    public List<User> getLastUsers(int size) {
        return userRepository.getLastCreatedUsers(Pageable.ofSize(size));
    }

    public long getUsersCount(){
        return userRepository.count();
    }

    public Optional<User> getUserByName(String name) {
        return userRepository.getUserByUsername(name);
    }

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

    private String createUuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    public int getActiveUsersCount() {
       
        return userRepository.getEnabledUsersCount();
    }

    public int getInactiveUsersCount(){
        return userRepository.getDisabledUsersCount();
    }

    public List<Integer> getUsersRegistrationMonth() {
        List<Integer> usersRegisrationMonth = null;
        try {
            usersRegisrationMonth = userRepository.getUsersRegistrationCountMonth();
        } catch (Exception e) {
            //throw new RuntimeException(e);
        }
        return usersRegisrationMonth;
    }

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
