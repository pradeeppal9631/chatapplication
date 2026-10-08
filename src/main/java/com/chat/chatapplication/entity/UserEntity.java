package com.chat.chatapplication.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;


@Document(collection = "chat_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {



    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    private String userEmail;
    private String userPassword;
}
