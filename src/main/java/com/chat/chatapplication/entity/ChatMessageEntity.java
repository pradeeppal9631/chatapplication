package com.chat.chatapplication.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.annotation.Collation;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "chat_message")
public class ChatMessageEntity {
     @Id
     private String id;

    private String sender;
    private String receiver;
    private String message;
    private LocalDateTime timestamp;
}
