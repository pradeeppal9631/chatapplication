package com.chat.chatapplication.chatController;

import com.chat.chatapplication.chatServices.UserServices;
import com.chat.chatapplication.dto.UserDto;
import com.chat.chatapplication.dto.UserStatusDto;
import com.chat.chatapplication.entity.UserEntity;
////import com.chatapplication.entity.User;
//import com.chatapplication.service.UserService;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private  UserServices userService;

    public UserController(UserServices userService) {
        this.userService = userService;
    }

    //@PostMapping
    //public UserEntity createUser(@RequestBody UserEntity user) {
        //return userService.createUser(user);
    //}

    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestBody UserEntity user) {

        try {

            UserEntity savedUser = userService.createUser(user);

            UserDto response = new UserDto();

            response.setId(savedUser.getId());
            response.setUsername(savedUser.getUsername());
            response.setUserEmail(savedUser.getUserEmail());

            return ResponseEntity.status(201).body(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(409)
                    .body(e.getMessage());
        }
    }

//    @GetMapping
//    public List<UserEntity> getAllUsers() {
//        return userService.getAllUsers();
//    }
@GetMapping("/me")
public ResponseEntity<UserDto> getCurrentUser(
        Authentication authentication) {

    String username = authentication.getName();

    Optional<UserEntity> userOptional =
            userService.getUserByUsername(username);

    if (userOptional.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    UserEntity user = userOptional.get();

    UserDto response =
            new UserDto();

    response.setId(user.getId());
    response.setUsername(user.getUsername());
    response.setUserEmail(user.getUserEmail());

    return ResponseEntity.ok(response);
}

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable String id) {

        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(
            @PathVariable String id,
            @RequestBody UserEntity user) {

        UserEntity updatedUser = userService.updateUser(id, user);

        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(
            @RequestBody UserEntity user) {

        UserEntity savedUser =
                userService.createUser(user);

        UserDto response =
                new UserDto();

        response.setId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setUserEmail(savedUser.getUserEmail());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/status/{username}")
    public ResponseEntity<UserStatusDto> getUserStatus(
            @PathVariable String username) {

        boolean online =
                userService.isUserOnline(username);

        UserStatusDto response =
                new UserStatusDto(
                        username,
                        online
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<UserDto> searchUser(
            @RequestParam String username,
            Authentication authentication) {

        // Login user ka username
        String currentUser = authentication.getName();

        // Apne aap ko search nahi kar sakta
        if (currentUser.equals(username)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<UserEntity> userOptional =
                userService.getUserByUsername(username);

        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserEntity user = userOptional.get();

        UserDto response = new UserDto();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setUserEmail(user.getUserEmail());

        return ResponseEntity.ok(response);
    }

}