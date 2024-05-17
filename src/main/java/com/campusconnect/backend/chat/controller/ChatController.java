package com.campusconnect.backend.chat.controller;

import com.campusconnect.backend.chat.dto.request.ChatRequest;
import com.campusconnect.backend.config.web.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final RabbitMqConfig rabbitMqConfig;

    private final static String CHAT_EXCHANGE_NAME = "chat.exchange";
    private final static String CHAT_QUEUE_NAME = "chat.queue";

    /** chatRoomId로 요청 시 Broker를 통해 Message 처리
     * /exchange/chat.exchange/room.{chatRoomId}를 subscribe한 클라이언트 측으로 메시지 전송
     */
    @MessageMapping("chat.enter.{chatRoomId}")
    public void createChatting(@Payload ChatRequest chatRequest, @DestinationVariable String chatRoomId) {
        chatRequest.setCreatedAt(LocalDateTime.now());
        rabbitMqConfig.rabbitTemplate()
                .convertAndSend(CHAT_EXCHANGE_NAME, "chat.room." + chatRoomId + chatRequest.getMessage());
    }

    @MessageMapping("chat.message.{chatRoomId}")
    public void sendMessage(@Payload ChatRequest chatRequest, @DestinationVariable String chatRoomId) {
        chatRequest.setCreatedAt(LocalDateTime.now());
        chatRequest.setMessage(chatRequest.getMessage());
        rabbitMqConfig.rabbitTemplate()
                .convertAndSend(CHAT_EXCHANGE_NAME, "chat.room.", chatRoomId + chatRequest.getMessage());
    }

    @RabbitListener(queues = CHAT_QUEUE_NAME)
    public void receiveMessage(ChatRequest chatRequest) {
        log.info("received = {}", chatRequest.getMessage());
    }


//    /** 채팅 리스트 */
//    @GetMapping("/chat/list")
//    public ResponseEntity<ChatListResponse> getChatRoomList() {
//        chatService.getChatRoomList();
//    }

}
