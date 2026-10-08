package com.chat.chatapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStatusDto {

    private String username;
    private boolean online;
}