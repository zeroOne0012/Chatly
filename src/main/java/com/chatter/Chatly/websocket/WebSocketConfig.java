package com.chatter.Chatly.websocket;

import com.chatter.Chatly.domain.channelmember.ChannelMemberService;
import com.chatter.Chatly.domain.chatroom.ChatRoomRepository;
import com.chatter.Chatly.domain.member.MemberRepository;
import com.chatter.Chatly.util.MemberContext;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

// 테스트 : https://jiangxy.github.io/websocket-debug-tool/

@Configuration
@EnableWebSocketMessageBroker
@AllArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final MemberContext memberContext;
    private final ChatRoomRepository chatRoomRepository;
    private final ChannelMemberService channelMemberService;
    private final WebSocketChannelInterceptor webSocketChannelInterceptor;
    private final MemberRepository memberRepository;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // WebSocket 엔드포인트 "/ws"에 대한 CORS 정책 설정
        registry
//                .setErrorHandler(stompExceptionHandler)
                .addEndpoint("/ws")
            .setAllowedOrigins("*")
                .addInterceptors(new HttpSessionHandshakeInterceptor())  // 원하는 출처로 CORS 허용
//            .addInterceptors(new WebSocketInterceptor(memberContext, chatRoomRepository, channelMemberService)) // WebSocket 연결 시 인증
///////////////////////////            .addInterceptors((HandshakeInterceptor) new WebSocketChannelInterceptor(memberRepository)); // WebSocket 연결 시 인증
//            .withSockJS();
//            .setInterceptors(new HttpSessionHandshakeInterceptor()); // 웹소켓 서브 프로토콜 전달 (HTTP 세션 전달), 세션
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");  // 메시지를 발행 (topic)
        registry.setApplicationDestinationPrefixes("/app");  // 클라이언트 -> 서버 destination의 접두사
    }

//    @Override
//    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
//            registry.addDecoratorFactory(new WebSocketInterceptorDecoratorFactory(new WebSocketInterceptor(memberContext, chatRoomRepository, channelMemberService)));
//    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketChannelInterceptor);
    }

    // ------

    // private final StompHandler stompHandler; // jwt 관련 로직 추가?

//    // STOMP 메시지 레벨에서 개입, stompHandler가 SEND 된 메시지를 가로채 인증 검증, 내용 수정 등 가능
//    // 매 메시지마다 처리할 작업 등에 적합
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        // registration.interceptors(stompHandler);
//    }
//
//    // Websocket 세션 수준의 개입, onMessage, onClose 같은 Websocket 연결 자체의 이벤트 감시 or 제어 구현
//    // 최초 1회 인증 등에 적합
//    @Override
//    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
//        registry.addDecoratorFactory(new WebSocketInterceptorDecoratorFactory());
//    }

    /*
    각 메시지
    @Override
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                // 메시지마다 사용자 인증 정보 확인
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                Principal user = accessor.getUser();

                if (user != null) {
                    // ex) user.getName() 기반으로 Entity 조회 및 처리
                }

                return message;
            }
        });
    }


    최초 1회
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.addDecoratorFactory(handler -> new WebSocketHandlerDecorator(handler) {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                // 최초 연결 감지
                String token = extractTokenFrom(session);
                authenticateAndSetPrincipal(session, token);
                super.afterConnectionEstablished(session);
            }
        });
    }


    * */
}