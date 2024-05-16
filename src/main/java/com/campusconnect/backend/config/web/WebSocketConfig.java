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

    private final WebSocketHandler webSocketHandler;
    private final RabbitMqConfig rabbitMqConfig;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Message Subscribe URL
        config.enableStompBrokerRelay("/exchange")
                .setClientLogin(rabbitMqConfig.getRabbitUser())
                .setClientPasscode(rabbitMqConfig.getRabbitPw())
                .setSystemLogin(rabbitMqConfig.getRabbitUser())
                .setSystemPasscode(rabbitMqConfig.getRabbitPw())
                .setRelayHost(rabbitMqConfig.getRabbitHost());
//                .setRelayPort(rabbitMqConfig.getRabbitPort());
//                .setVirtualHost(rabbitMqConfig.getRabbitVh());

        // Message Publish URL
        config.setPathMatcher(new AntPathMatcher("."));
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
//                .withSockJS();
    }
}
