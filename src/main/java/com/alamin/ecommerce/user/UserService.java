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

    public Optional<User> findById(Long id) {
        if (id == null)
            return Optional.empty();

        return userRepository.findById(id);
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

    private String generateFileUuid(){
        return UUID.randomUUID().toString();
    }

    public List<User> getLastUsers(int i) {

        return userRepository.getLastUsers(Pageable.ofSize(i));
    }

    public long findCount(){
        return userRepository.count();
    }
}
