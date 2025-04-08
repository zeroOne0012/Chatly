// package com.chatter.Chatly.websocket;
//
// import org.slf4j.LoggerFactory;
// import org.springframework.context.event.EventListener;
// import org.springframework.stereotype.Component;
// import org.springframework.web.socket.messaging.SessionConnectEvent;
// import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
// import java.util.Objects;
// import java.util.logging.Logger;
//
// @Component
// public class WebSocketEventListener {
//
//     private static final Logger logger = (Logger) LoggerFactory.getLogger(WebSocketEventListener.class);
//
//     @EventListener
//     public void handleSessionConnected(SessionConnectEvent event) {
//         logger.info("New WebSocket Connection: " + Objects.requireNonNull(event.getUser()).getName());
//     }
//
//     @EventListener
//     public void handleSessionDisconnect(SessionDisconnectEvent event) {
//         logger.info("WebSocket Disconnected: " + Objects.requireNonNull(event.getUser()).getName());
//     }
// }
