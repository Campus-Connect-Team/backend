package com.campusconnect.backend.chat.controller;

import com.campusconnect.backend.chat.dto.request.ChatRequest;
import com.campusconnect.backend.chat.service.ChatService;
import com.campusconnect.backend.config.web.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final RabbitMqConfig rabbitMqConfig;

    private final static String CHAT_EXCHANGE_NAME = "chat.exchange";
    private final static String CHAT_QUEUE_NAME = "chat.queue";

    private final ChatService chatService;

    /** chatRoomId로 요청 시 Broker를 통해 Message 처리
     * /exchange/chat.exchange/room.{chatRoomId}를 subscribe한 클라이언트 측으로 메시지 전송
     */
    @MessageMapping("chat.enter.{chatRoomId}")
    public void createChatting(@Payload ChatRequest chatRequest, @DestinationVariable String chatRoomId) {
        chatRequest.setCreatedAt(LocalDateTime.now());
        rabbitMqConfig.rabbitTemplate()
                .convertAndSend(CHAT_EXCHANGE_NAME, "chat.room." + chatRoomId + chatRequest.getMessage());
    }
}
