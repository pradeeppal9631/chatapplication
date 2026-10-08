package com.chat.chatapplication.chatServices;

import com.chat.chatapplication.chatRepository.ChatMessageRepo;
import com.chat.chatapplication.entity.ChatMessageEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Chathistoryservices {

    private final ChatMessageRepo chatMessageRepo;

    public Chathistoryservices(ChatMessageRepo chatMessageRepo) {
        this.chatMessageRepo = chatMessageRepo;
    }

    public List<ChatMessageEntity> getChatHistory(
            String user1,
            String user2) {

        return chatMessageRepo
                .findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
                        user1,
                        user2,
                        user2,
                        user1
                );
    }
}