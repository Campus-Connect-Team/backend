package com.campusconnect.backend.config.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final RabbitMqConfig rabbitMqConfig;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // message subscribe
        config.enableStompBrokerRelay("/exchange")
                .setClientLogin(rabbitMqConfig.getRabbitUser())
                .setClientPasscode(rabbitMqConfig.getRabbitPw())
                .setSystemLogin(rabbitMqConfig.getRabbitUser())
                .setSystemPasscode(rabbitMqConfig.getRabbitPw())
                .setRelayHost(rabbitMqConfig.getRabbitHost())
                .setRelayPort(rabbitMqConfig.getRabbitPort());

        // message publish
        config.setPathMatcher(new AntPathMatcher("."));
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }

//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.setApplicationDestinationPrefixes("/send");  // 클라이언트에서 보낼 메시지를 받을 /prefix
//        registry.enableSimpleBroker("/chatRoomId");  // 해당 주소를 구독하는 클라이언트에게 메시지 전달
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws-stomp")
//                .setAllowedOriginPatterns("*")
//                .withSockJS();
//    }
}
