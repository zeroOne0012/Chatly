package com.chatter.Chatly.websocket;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

public class WebSocketInterceptorDecorator extends WebSocketHandlerDecorator {

    private final WebSocketInterceptor webSocketInterceptor;

    public WebSocketInterceptorDecorator(WebSocketHandler delegate, WebSocketInterceptor webSocketInterceptor) {
        super(delegate);
        this.webSocketInterceptor = webSocketInterceptor;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

//        // WebSocket 세션에 있는 userId와 chatRoomId 추출, 사용 가능
//        String userId = (String) session.getAttributes().get("userId");
//        Integer chatRoomId = (Integer) session.getAttributes().get("chatRoomId");
//
//        // 세션 정보 활용 (예: 메세지 처리 시 해당 정보 사용)
//        // 예: 채팅방에 사용자가 입장했다는 로그를 남김 등
//        System.out.println("User " + userId + " entered chat room " + chatRoomId);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
        // 메시지 처리 시 추가 로직
    }
}
