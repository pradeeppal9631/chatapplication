package com.chat.chatapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Chatapplication {

    public static void main(String[] args) {
        SpringApplication.run(Chatapplication.class, args);
    }
}
