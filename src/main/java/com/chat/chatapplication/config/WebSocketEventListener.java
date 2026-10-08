package com.chat.chatapplication.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @EventListener
    public void onConnect(SessionConnectEvent event) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        System.out.println("========== CONNECT EVENT ==========");

        System.out.println(
                "Session ID = " +
                        accessor.getSessionId()
        );

        System.out.println(
                "User = " +
                        accessor.getUser()
        );
    }


    @EventListener
    public void onConnected(SessionConnectedEvent event) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        System.out.println("========== CONNECTED EVENT ==========");

        System.out.println(
                "Session ID = " +
                        accessor.getSessionId()
        );

        System.out.println(
                "User = " +
                        accessor.getUser()
        );
    }


    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {

        System.out.println("========== DISCONNECT EVENT ==========");

        System.out.println(
                "Session ID = " +
                        event.getSessionId()
        );

        System.out.println(
                "User = " +
                        event.getUser()
        );
    }
}