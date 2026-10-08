package com.chat.chatapplication.chatController;


import com.chat.chatapplication.chatServices.RedisServices;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis")
public class RedisController {
    private final RedisServices redisService;

    public RedisController(RedisServices  redisService) {
        this.redisService = redisService;
    }

    @PostMapping("/server")
    public String setServer(
            @RequestParam String username,
            @RequestParam String serverId) {

        redisService.setUserServer(username, serverId);

        return username + " is connected to " + serverId;
    }

    @GetMapping("/server/{username}")
    public String getServer(
            @PathVariable String username) {

        String serverId =
                redisService.getUserServer(username);

        if (serverId == null) {
            return "User is not online";
        }

        return username + " is connected to " + serverId;
    }

    @DeleteMapping("/server/{username}")
    public String removeServer(
            @PathVariable String username) {

        redisService.removeUser(username);

        return username + " removed from Redis";
    }

}
