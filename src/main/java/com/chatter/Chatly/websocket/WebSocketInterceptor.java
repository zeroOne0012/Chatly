 package com.chatter.Chatly.websocket;

 import com.chatter.Chatly.domain.channel.Channel;
 import com.chatter.Chatly.domain.channelmember.ChannelMember;
 import com.chatter.Chatly.domain.channelmember.ChannelMemberService;
 import com.chatter.Chatly.domain.chatroom.ChatRoom;
 import com.chatter.Chatly.domain.chatroom.ChatRoomRepository;
 import com.chatter.Chatly.exception.ResourceNotFoundException;
 import com.chatter.Chatly.util.MemberContext;
 import org.springframework.http.server.ServerHttpRequest;
 import org.springframework.http.server.ServerHttpResponse;
 import org.springframework.web.socket.WebSocketHandler;
 import org.springframework.web.socket.server.HandshakeInterceptor;

 import java.util.Map;

 public class WebSocketInterceptor implements HandshakeInterceptor {

     private final MemberContext memberContext;
     private final ChatRoomRepository chatRoomRepository;
     private final ChannelMemberService channelMemberService;

     public WebSocketInterceptor(MemberContext memberContext, ChatRoomRepository chatRoomRepository, ChannelMemberService channelMemberService) {
         this.memberContext = memberContext;
         this.chatRoomRepository = chatRoomRepository;
         this.channelMemberService = channelMemberService;
     }

     @Override
     public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                    WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

         // JWT 토큰에서 MemberId 추출
         String memberId = memberContext.getMemberIdFromRequest();
         if (memberId == null) {
             return false; // JWT가 유효하지 않으면 연결 거부
         }

        // chatRoomId 추출 (헤더나 쿼리 파라미터에서 추출)
         String chatRoomIdStr = request.getURI().getQuery();  // URL에서 query 파라미터로 추출
         if (chatRoomIdStr == null || chatRoomIdStr.isEmpty()) {
             return false;  // chatRoomId가 없으면 연결 거부
         }
         Long chatRoomId = null;
         try {
             chatRoomId = Long.parseLong(chatRoomIdStr);
         } catch (NumberFormatException e) {
             return false;  // chatRoomId가 Long으로 변환되지 않으면 연결 거부
         }

         // chatRoom -> Channel
         Long finalChatRoomId = chatRoomId; // Variable used in lambda expression should be final or effectively final !!
         ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                 .orElseThrow(() -> new ResourceNotFoundException("ChatRoom not found with ID: " + finalChatRoomId));
         Channel channel = chatRoom.getChannel();

         ChannelMember cm = channelMemberService.isJoined(channel.getId(), memberId);
         if(cm==null){
             return false; // 채널에 속하지 않은 회원은 연결 거부
         }

         // WebSocket 세션에 memberId, chatRoomId 저장
         attributes.put("memberId", memberId);
         attributes.put("chatRoomId", chatRoomId);
         return true;
     }

     @Override
     public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                WebSocketHandler wsHandler, Exception exception) {
         // Handshake 이후 추가 작업
         // Override 필수
     }
 }
