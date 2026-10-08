package com.chat.chatapplication.chatController;
import jakarta.servlet.http.HttpServletRequest;

//import com.chat.chatapplication.chatServices.RedisServices;
import com.chat.chatapplication.chatServices.UserServices;
import com.chat.chatapplication.jwtsecurity.JwtServices;
import io.jsonwebtoken.Jwt;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.chat.chatapplication.dto.InfoDto;
import com.chat.chatapplication.chatServices.RedisServices;
import org.springframework.security.core.Authentication;
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private final UserServices userServices;
    private final JwtServices jwtServices;
    private final RedisServices redisServices;

    public AuthController(UserServices userServices, JwtServices jwtServices,RedisServices redisServices){
        this.userServices=userServices;
        this.jwtServices=jwtServices;
        this.redisServices=redisServices;
    }
    @PostMapping("/login")
    public  ResponseEntity< ?> login(
            @RequestBody InfoDto request) {

        boolean authenticated = userServices.login(request.getUsername(), request.getPassword());

        if (authenticated) {
            String token= jwtServices.generateToken(
                    request.getUsername()
            );
            return ResponseEntity.ok(token);
        }

        return ResponseEntity.status(401)
                .body("Invalid username or password");
    }

//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(
//            Authentication authentication) {
//
//        String username = authentication.getName();
//
//        redisServices.removeUser(username);
//
//        return ResponseEntity.ok("Logout successful");
//    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            Authentication authentication,
            HttpServletRequest request) {

        if (authentication == null) {

            return ResponseEntity
                    .status(401)
                    .body("User is not authenticated");
        }


        String username =
                authentication.getName();


        String authHeader =
                request.getHeader("Authorization");


        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            return ResponseEntity
                    .badRequest()
                    .body("Authorization token missing");
        }


        String token =
                authHeader.substring(7);


        // JWT ke remaining time ko calculate karo

        long remainingSeconds =
                jwtServices.getRemainingExpiration(token);


        // Redis blacklist

        redisServices.blacklistToken(
                token,
                remainingSeconds
        );


        // Online status remove

        redisServices.removeUser(username);


        return ResponseEntity.ok(
                "Logout successful"
        );
    }


}
