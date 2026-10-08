package com.chat.chatapplication.config;

import com.chat.chatapplication.chatServices.RedisServices;
import com.chat.chatapplication.jwtsecurity.JwtServices;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.stereotype.Component;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtServices jwtServices;
    private final RedisServices redisServices;

    public JwtChannelInterceptor(
            JwtServices jwtServices,
            RedisServices redisServices) {

        this.jwtServices = jwtServices;
        this.redisServices = redisServices;
    }

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                );

        if (accessor == null) {
            return message;
        }

        StompCommand command =
                accessor.getCommand();

        System.out.println(
                "STOMP command = " + command
        );


        if (StompCommand.CONNECT.equals(command)) {

            String authorization =
                    accessor.getFirstNativeHeader(
                            "Authorization"
                    );

            System.out.println(
                    "Authorization = " +
                            authorization
            );


            if (authorization == null ||
                    !authorization.startsWith("Bearer ")) {

                System.out.println(
                        "JWT missing"
                );

                return null;
            }


            try {

                String token =
                        authorization.substring(7);


                if (redisServices.isTokenBlacklisted(token)) {

                    System.out.println(
                            "WebSocket JWT is blacklisted"
                    );

                    return null;
                }


                String username =
                        jwtServices.extractUsername(
                                token
                        );

                System.out.println(
                        "WebSocket user = " +
                                username
                );


                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                null
                        );


                // Set authenticated user
                accessor.setUser(
                        authentication
                );


                System.out.println(
                        "Principal set = " +
                                accessor.getUser()
                );


                // Save username in session
                if (accessor.getSessionAttributes() != null) {

                    accessor.getSessionAttributes()
                            .put(
                                    "username",
                                    username
                            );
                }


                // Redis presence
                String serverId =
                        "server-1";

                redisServices.setUserServer(
                        username,
                        serverId
                );


                System.out.println(
                        "User registered in Redis: " +
                                username +
                                " -> " +
                                serverId
                );


            } catch (Exception e) {

                System.out.println(
                        "JWT ERROR = " +
                                e.getMessage()
                );

                e.printStackTrace();

                return null;
            }
        }



        if (StompCommand.SUBSCRIBE.equals(command)) {

            System.out.println(
                    "SUBSCRIBE received"
            );

            System.out.println(
                    "Current user before restore = " +
                            accessor.getUser()
            );


            if (accessor.getUser() == null &&
                    accessor.getSessionAttributes() != null) {

                String username =
                        (String) accessor
                                .getSessionAttributes()
                                .get("username");


                System.out.println(
                        "Username from session = " +
                                username
                );


                if (username != null) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    null
                            );

                    accessor.setUser(
                            authentication
                    );


                    System.out.println(
                            "Principal restored for SUBSCRIBE = " +
                                    username
                    );
                }
            }
        }


        if (StompCommand.SEND.equals(command)) {

            System.out.println(
                    "SEND received"
            );

            System.out.println(
                    "Current user before restore = " +
                            accessor.getUser()
            );


            if (accessor.getUser() == null &&
                    accessor.getSessionAttributes() != null) {

                String username =
                        (String) accessor
                                .getSessionAttributes()
                                .get("username");


                System.out.println(
                        "Username from session = " +
                                username
                );


                if (username != null) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    null
                            );

                    accessor.setUser(
                            authentication
                    );


                    System.out.println(
                            "Principal restored for SEND = " +
                                    username
                    );
                }
            }
        }


        if (StompCommand.DISCONNECT.equals(command)) {

            System.out.println(
                    "Processing DISCONNECT..."
            );


            if (accessor.getSessionAttributes() != null) {

                String username =
                        (String) accessor
                                .getSessionAttributes()
                                .get("username");


                System.out.println(
                        "Username from session = " +
                                username
                );


                if (username != null) {

                    redisServices.removeUser(
                            username
                    );


                    System.out.println(
                            "User disconnected = " +
                                    username
                    );
                }
            }
        }


        System.out.println(
                "FINAL USER = " +
                        accessor.getUser()
        );


        return message;
    }
}