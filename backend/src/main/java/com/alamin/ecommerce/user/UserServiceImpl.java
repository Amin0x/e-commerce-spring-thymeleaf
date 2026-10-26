package com.alamin.ecommerce.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> getAllUsers(int page, int size) {
        if (page < 0 || size <= 0)
            throw new IllegalArgumentException("");

        return userRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUUID(String uuid) {
        if (uuid == null)
            throw new IllegalArgumentException("User UUID cannot be null");

        return userRepository.getUserByUuid(uuid);
    }

    @Override
    public int getCreatedUsersCountThisMonth() {
        int usersThisMonth = 0;

        try {
            usersThisMonth = userRepository.getCreatedUsersCountThisMonth();

        } catch (Exception e) {
            //throw new RuntimeException(e);
        }

        return usersThisMonth;
    }

    @Override
    public List<User> getLastRegisteredUsers(int size) {
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

        User createdUser = new User();
        if(List.of("USER", "ADMIN", "MODERATOR").contains(user.getRole())){
            createdUser.setRole(user.getRole());
        } else {
            log.warn("Invalid role provided, defaulting to USER");
            createdUser.setRole("USER");
        }
        createdUser.setFirstName(user.getFirstName());
        createdUser.setLastName(user.getLastName());
        createdUser.setUsername(user.getUsername());
        createdUser.setPassword(passwordEncoder.encode(user.getPassword()));

        createdUser.setEmail(user.getEmail());
        createdUser.setStatus(user.getStatus());
        createdUser.setEnabled(user.getEnabled());
        createdUser.setBirthDate(user.getBirthDate());
        createdUser.setUuid(this.createUuid());

        return userRepository.save(createdUser);
    }

    @Override
    public User createUser(SignupForm signupForm) {
        log.info("signupForm: {}", signupForm);
        User user = new User();
        user.setUsername(signupForm.getUsername());
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setRole("USER"); //defuel user role
        user.setEnabled(true);
        user.setStatus("NOT_VERIFIED");
        user.setEmail(null);
        user.setAvatar(null);
        user.setFirstName(null);
        user.setLastName(null);
        user.setBirthDate(null);
        user.setCreated(LocalDateTime.now());
        user.setUpdated(null);
        user.setUuid(createUuid());
        return userRepository.save(user);
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
        User updatedUser = getUserById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        validateUser(user);
        updatedUser.setFirstName(user.getFirstName());
        updatedUser.setLastName(user.getLastName());
        //updatedUser.setUsername(user.getUsername());
        updatedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        updatedUser.setEmail(user.getEmail());
        updatedUser.setStatus(user.getStatus());
        updatedUser.setEnabled(user.getEnabled());
        updatedUser.setBirthDate(user.getBirthDate());
        updatedUser.setRole(user.getRole());
        //updatedUser.setLastActive();
        updatedUser.setUpdated(LocalDateTime.now());
        return userRepository.save(updatedUser);
    }

    public void sendConformationEmail(User user){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("");
        message.setTo("");
        message.setSubject("User Registration Confirmation");
        message.setText("""
                Dear ${user.getFirstName()}
                
                Thank you for registering on our website. Your account has been created successfully.
                Please click the link below to confirm your email address and activate your account:
                http://localhost:8080/auth/users/confirm?token=${user.getUuid()}&email=${user.getEmail()}
                Best regards,
                The Team""");
        //emailSender.send(message);
    }

    //
    private String createUuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public int getEnabledUsersCount() {
        return userRepository.getEnabledUsersCount();
    }

    // This method is used to get the inactive users count
    @Override
    public int getDisabledUsersCount(){
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

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    
}
