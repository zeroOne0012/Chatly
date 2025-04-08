package com.chatter.Chatly.websocket;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

public class WebSocketInterceptorDecoratorFactory implements WebSocketHandlerDecoratorFactory {

    private final WebSocketInterceptor webSocketInterceptor;

    public WebSocketInterceptorDecoratorFactory(WebSocketInterceptor webSocketInterceptor) {
        this.webSocketInterceptor = webSocketInterceptor;
    }

    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {
        return new WebSocketInterceptorDecorator(handler, webSocketInterceptor);
    }
}

