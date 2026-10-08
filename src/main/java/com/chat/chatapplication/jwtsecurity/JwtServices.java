package com.chat.chatapplication.jwtsecurity;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
//import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtServices {
    @Value("${jwt.secret}")
    private  String secret;


    private  SecretKey getKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }


    public  String generateToken(String username) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + 1000 * 60 * 60)
                )
                .signWith(getKey())
                .compact();
    }


    public String extractUsername(String token) {

        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }


    public long getRemainingExpiration(String token) {

        Date expiration =
                Jwts.parser()
                        .verifyWith(getKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getExpiration();

        long remaining =
                expiration.getTime() - System.currentTimeMillis();

        return Math.max(
                1,
                remaining / 1000
        );
    }

}
