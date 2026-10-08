package com.chat.chatapplication.chatController;

import com.chat.chatapplication.chatServices.Chathistoryservices;
import com.chat.chatapplication.entity.ChatMessageEntity;
import com.chat.chatapplication.chatRepository.ChatMessageRepo;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatMessageRepo chatMessageRepository;

    private final Chathistoryservices chathistoryservices;


    public ChatController(
            SimpMessagingTemplate messagingTemplate,
            ChatMessageRepo chatMessageRepository,
            Chathistoryservices chathistoryservices) {

        this.messagingTemplate =
                messagingTemplate;

        this.chatMessageRepository =
                chatMessageRepository;

        this.chathistoryservices =
                chathistoryservices;
    }


    // =====================================================
    // SEND PRIVATE MESSAGE
    // =====================================================

    @org.springframework.messaging.handler.annotation.MessageMapping("/chat")
    public void sendMessage(
            ChatMessageEntity chatMessage,
            Message<?> message) {


        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(message);


        String sender = null;


        // -------------------------------------------------
        // Get sender from WebSocket Principal
        // -------------------------------------------------

        if (accessor.getUser() != null) {

            sender =
                    accessor
                            .getUser()
                            .getName();
        }


        // -------------------------------------------------
        // Fallback: session username
        // -------------------------------------------------

        if (sender == null &&
                accessor.getSessionAttributes() != null) {

            sender =
                    (String) accessor
                            .getSessionAttributes()
                            .get("username");
        }


        System.out.println(
                "Sender from WebSocket = " +
                        sender
        );


        // -------------------------------------------------
        // Sender validation
        // -------------------------------------------------

        if (sender == null) {

            System.out.println(
                    "ERROR: Sender is null"
            );

            return;
        }


        // -------------------------------------------------
        // Set sender from JWT
        // -------------------------------------------------

        chatMessage.setSender(
                sender
        );


        // -------------------------------------------------
        // Timestamp
        // -------------------------------------------------

        chatMessage.setTimestamp(
                LocalDateTime.now()
        );


        // -------------------------------------------------
        // Save message
        // -------------------------------------------------

        ChatMessageEntity savedMessage =
                chatMessageRepository.save(
                        chatMessage
                );


        System.out.println(
                "Message saved in MongoDB: " +
                        savedMessage
        );


        // -------------------------------------------------
        // Send private message
        // -------------------------------------------------

        System.out.println(
                "Sending message to user = " +
                        savedMessage.getReceiver()
        );


        messagingTemplate.convertAndSendToUser(

                savedMessage.getReceiver(),

                "/queue/messages",

                savedMessage

        );

        messagingTemplate.convertAndSendToUser(
                savedMessage.getSender(),
                "/queue/messages",
                savedMessage
        );
    }


    // =====================================================
    // SECURE CHAT HISTORY
    // =====================================================

    @GetMapping("/chat/history/{otherUser}")
    @ResponseBody
    public ResponseEntity<List<ChatMessageEntity>> getChatHistory(

            @PathVariable String otherUser,

            Authentication authentication) {


        // -------------------------------------------------
        // Check authentication
        // -------------------------------------------------

        if (authentication == null ||
                authentication.getName() == null) {

            return ResponseEntity
                    .status(401)
                    .build();
        }


        // -------------------------------------------------
        // Current logged-in user
        // -------------------------------------------------

        String currentUser =
                authentication.getName();


        System.out.println(
                "========== CHAT HISTORY =========="
        );


        System.out.println(
                "Current user = " +
                        currentUser
        );


        System.out.println(
                "Other user = " +
                        otherUser
        );


        // -------------------------------------------------
        // Get history
        // -------------------------------------------------

        List<ChatMessageEntity> history =
                chathistoryservices.getChatHistory(
                        currentUser,
                        otherUser
                );


        System.out.println(
                "History size = " +
                        history.size()
        );


        return ResponseEntity.ok(
                history
        );
    }
}