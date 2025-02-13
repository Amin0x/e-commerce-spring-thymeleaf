package com.alamin.ecommerce.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findAll(int page, int size) {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        if (id == null)
            return Optional.empty();

        return userRepository.findById(id);
    }

    public Optional<User> findUserByUUID(String id) {
        if (id == null)
            return Optional.empty();

        return userRepository.findByUuid(id);
    }

    public int getUsersThisMonth() {
        int usersThisMonth = 0;

        try {
            usersThisMonth = userRepository.getUsersThisMonth();

        } catch (Exception e) {
            //throw new RuntimeException(e);
        }

        return usersThisMonth;
    }

    private void saveFile(MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                // Save the file to a directory
                byte[] bytes = file.getBytes();
                Path path = Paths.get("uploads/" + file.getOriginalFilename());
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }



    public List<User> getLastUsers(int size) {
        return userRepository.getLastUsers(Pageable.ofSize(size));
    }

    public long getUsersCount(){
        return userRepository.count();
    }

    public Optional<User> findUserByName(String name) {
        return userRepository.findByUsername(name);
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
        return userRepository.save(createdUser);
    }

    public User updateUser(Long id, User user) {
        User upatedUser = findUserById(id).orElseThrow();
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
        return UUID.randomUUID().toString();
    }
}
