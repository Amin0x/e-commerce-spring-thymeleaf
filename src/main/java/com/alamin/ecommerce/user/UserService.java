package com.alamin.ecommerce.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public int getUsersCountThisMonth() {
        int usersThisMonth = 0;

        try {
            usersThisMonth = userRepository.getUsersCountThisMonth();

        } catch (Exception e) {
            //throw new RuntimeException(e);
        }

        return usersThisMonth;
    }

    private String saveFile(byte[] bytes, String fileName) throws IOException {
        Path path = null;
        String strPath = "uploads/" + fileName;
        try {
            // Save the file to a directory
            path = Paths.get(strPath);
            Files.write(path, bytes);
        } catch (IOException e) {
            log.error("Error saving file " + fileName, e);
            throw e;
        }
        return strPath;
    }




    public List<User> getLastUsers(int size) {
        return userRepository.getLastRegisteredUsers(Pageable.ofSize(size));
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
        createdUser.setUuid(this.createUuid());
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

    public int getActiveUsersCount() {
       
        return userRepository.getActiveUsersCount();
    }
}
