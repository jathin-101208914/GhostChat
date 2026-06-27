package com.ghostchat.service;

import com.ghostchat.websocket.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MessageExpiryService {

    private final MessageService messageService;

    public void expireMessage(ChatMessage message){
        CompletableFuture.delayedExecutor(30, TimeUnit.SECONDS).execute(() -> {
            messageService.deleteMessage(message);

            System.out.println("MESSAGE EXPIRED -> " + message.getContent());
        });
    }
}
