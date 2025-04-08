//package com.chatter.Chatly.websocket;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
//
//@Component
//public class StompExceptionHandler extends StompSubProtocolErrorHandler {
//
//    private static final byte[] EMPTY_PAYLOAD = new byte[0];
//
//    public StompExceptionHandler() {
//        super();
//    }
//
//    @Override
//    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage,
//                                                              Throwable ex) {
//
//        final Throwable exception = converterTrowException(ex);
//
//        if (exception instanceof UnauthorizedException) {
//            return handleUnauthorizedException(clientMessage, exception);
//        }
//
//        return super.handleClientMessageProcessingError(clientMessage, ex);
//
//    }
//
//    private Throwable converterTrowException(final Throwable exception) {
//        if (exception instanceof MessageDeliveryException) {
//            return exception.getCause();
//        }
//        return exception;
//    }
//
//    private Message<byte[]> handleUnauthorizedException(Message<byte[]> clientMessage,
//                                                        Throwable ex) {
//
//        return prepareErrorMessage(clientMessage, ex.getMessage(), HttpStatus.UNAUTHORIZED.name());
//
//    }
//
//    private Message<byte[]> prepareErrorMessage(final Message<byte[]> clientMessage,
//                                                final String message, final String errorCode) {
//
//        final StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
//        accessor.setMessage(errorCode);
//        accessor.setLeaveMutable(true);
//
//        setReceiptIdForClient(clientMessage, accessor);
//
//        return MessageBuilder.createMessage(
//                message != null ? message.getBytes(StandardCharsets.UTF_8) : EMPTY_PAYLOAD,
//                accessor.getMessageHeaders()
//        );
//    }
//
//    private void setReceiptIdForClient(final Message<byte[]> clientMessage,
//                                       final StompHeaderAccessor accessor) {
//
//        if (Objects.isNull(clientMessage)) {
//            return;
//        }
//
//        final StompHeaderAccessor clientHeaderAccessor = MessageHeaderAccessor.getAccessor(
//                clientMessage, StompHeaderAccessor.class);
//
//        final String receiptId =
//                Objects.isNull(clientHeaderAccessor) ? null : clientHeaderAccessor.getReceipt();
//
//        if (receiptId != null) {
//            accessor.setReceiptId(receiptId);
//        }
//    }
//
//    //2
//    @Override
//    protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor,
//                                             byte[] errorPayload, Throwable cause, StompHeaderAccessor clientHeaderAccessor) {
//
//        return MessageBuilder.createMessage(errorPayload, errorHeaderAccessor.getMessageHeaders());
//
////        return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
//    }
//}
//출처: https://thalals.tistory.com/446 [힘차게, 열심히 공대생:티스토리]
