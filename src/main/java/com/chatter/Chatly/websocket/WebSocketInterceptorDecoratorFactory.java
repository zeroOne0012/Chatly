package com.chatter.Chatly.websocket;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

public class WebSocketInterceptorDecoratorFactory implements WebSocketHandlerDecoratorFactory {

    private final WebSocketChannelInterceptor webSocketChannelInterceptor;

    public WebSocketInterceptorDecoratorFactory(WebSocketChannelInterceptor webSocketChannelInterceptor) {
        this.webSocketChannelInterceptor = webSocketChannelInterceptor;
    }

    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {
        return new WebSocketInterceptorDecorator(handler, webSocketChannelInterceptor);
    }
}

