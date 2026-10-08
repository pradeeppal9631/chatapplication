package com.chat.chatapplication.chatRepository;

import com.chat.chatapplication.entity.ChatMessageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepo
        extends MongoRepository<ChatMessageEntity, String> {

    List<ChatMessageEntity>
    findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
            String sender1,
            String receiver1,
            String receiver2,
            String sender2
    );
}