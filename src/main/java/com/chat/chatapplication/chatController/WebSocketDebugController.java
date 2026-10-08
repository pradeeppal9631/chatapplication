package com.chat.chatapplication.chatController;

import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketDebugController {

    private final SimpUserRegistry simpUserRegistry;

    public WebSocketDebugController(
            SimpUserRegistry simpUserRegistry) {

        this.simpUserRegistry = simpUserRegistry;
    }

    @GetMapping("/debug/ws-users")
    public String getConnectedUsers() {

        System.out.println(
                "========== SIMP USER REGISTRY =========="
        );

        System.out.println(
                "Registry class = " +
                        simpUserRegistry
                                .getClass()
                                .getName()
        );

        System.out.println(
                "Total users = " +
                        simpUserRegistry.getUserCount()
        );

        System.out.println(
                "Users object = " +
                        simpUserRegistry.getUsers()
        );


        for (SimpUser user :
                simpUserRegistry.getUsers()) {

            System.out.println(
                    "User name = " +
                            user.getName()
            );

            System.out.println(
                    "Session count = " +
                            user.getSessions().size()
            );
        }


        return simpUserRegistry
                .getUsers()
                .stream()
                .map(SimpUser::getName)
                .toList()
                .toString();
    }
}