package com.chat.chatapplication.chatRepository;

import com.chat.chatapplication.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository  extends MongoRepository<UserEntity,String> {
    Optional<UserEntity> findByUsername(String username);

}
