package com.chat.chatapplication;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;

public class SecretkeyGen {
    public static void main(String[] args) {

        SecretKey key = Keys.secretKeyFor(
                io.jsonwebtoken.SignatureAlgorithm.HS256
        );

        String secret = Base64.getEncoder()
                .encodeToString(key.getEncoded());

        System.out.println(secret);
    }
}
