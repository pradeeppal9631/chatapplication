package com.chat.chatapplication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig
        implements WebSocketMessageBrokerConfigurer {

    private final JwtChannelInterceptor jwtChannelInterceptor;

    public WebSocketConfig(
            JwtChannelInterceptor jwtChannelInterceptor) {

        this.jwtChannelInterceptor = jwtChannelInterceptor;
    }

    @Override
    public void configureClientInboundChannel(
            org.springframework.messaging.simp.config.ChannelRegistration registration) {

        registration.interceptors(
                jwtChannelInterceptor
        );
    }

    @Override
    public void configureMessageBroker(
            MessageBrokerRegistry config) {

        // Messages sent to /topic or /queue
        // are handled by Spring's simple broker
        config.enableSimpleBroker(
                "/topic",
                "/queue"
        );

        // Client -> Server
        // /app/chat
        config.setApplicationDestinationPrefixes(
                "/app"
        );

        // Private user destination
        // /user/queue/messages
        config.setUserDestinationPrefix(
                "/user"
        );
    }

    @Override
    public void registerStompEndpoints(
            StompEndpointRegistry registry) {

        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }
}