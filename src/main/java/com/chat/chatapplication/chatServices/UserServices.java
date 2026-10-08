package com.chat.chatapplication.chatServices;

import com.chat.chatapplication.chatRepository.UserRepository;
import com.chat.chatapplication.entity.UserEntity;
import com.mongodb.client.MongoIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServices {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisServices redisServices;
    @Autowired
    public UserServices(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,RedisServices redisServices) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisServices=redisServices;
    }

    // =========================
    // REGISTER
    // =========================
    public UserEntity createUser(UserEntity user) {

        if (user.getUsername() == null ||
                user.getUsername().isBlank()) {

            throw new RuntimeException("Username is required");
        }

        if (user.getUserPassword() == null ||
                user.getUserPassword().isBlank()) {

            throw new RuntimeException("Password is required");
        }

        Optional<UserEntity> existingUser =
                userRepository.findByUsername(user.getUsername());

        if (existingUser.isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        String encodedPassword =
                passwordEncoder.encode(user.getUserPassword());

        user.setUserPassword(encodedPassword);

        return userRepository.save(user);
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UserEntity> getUserById(String id) {
        return userRepository.findById(id);
    }

    public UserEntity updateUser(String id, UserEntity user) {

        Optional<UserEntity> existingUser =
                userRepository.findById(id);

        if (existingUser.isEmpty()) {
            return null;
        }

        UserEntity oldUser = existingUser.get();

        oldUser.setUsername(user.getUsername());
        oldUser.setUserEmail(user.getUserEmail());

        if (user.getUserPassword() != null &&
                !user.getUserPassword().isBlank()) {

            oldUser.setUserPassword(
                    passwordEncoder.encode(
                            user.getUserPassword()
                    )
            );
        }

        return userRepository.save(oldUser);
    }



    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }


    public boolean login(String username, String password) {

        // First validate request
        if (username == null || username.isBlank()) {
            return false;
        }

        if (password == null || password.isBlank()) {
            return false;
        }

        // Find user
        Optional<UserEntity> userFind =
                userRepository.findByUsername(username);

        if (userFind.isEmpty()) {
            return false;
        }

        UserEntity user = userFind.get();

        // Check DB password
        if (user.getUserPassword() == null ||
                user.getUserPassword().isBlank()) {

            return false;
        }

        return passwordEncoder.matches(
                password,
                user.getUserPassword()
        );
    }

    public Optional<UserEntity> getUserByUsername(
            String username) {

        return userRepository.findByUsername(username);
    }

    public boolean isUserOnline(String username) {

        String server =
                redisServices.getUserServer(username);

        return server != null;
    }


}