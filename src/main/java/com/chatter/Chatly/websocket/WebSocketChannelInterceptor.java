 package com.chatter.Chatly.websocket;

 import com.chatter.Chatly.domain.channel.Channel;
 import com.chatter.Chatly.domain.channelmember.ChannelMember;
 import com.chatter.Chatly.domain.channelmember.ChannelMemberService;
 import com.chatter.Chatly.domain.chatroom.ChatRoom;
 import com.chatter.Chatly.domain.chatroom.ChatRoomRepository;
 import com.chatter.Chatly.domain.member.Member;
 import com.chatter.Chatly.domain.member.MemberRepository;
 import com.chatter.Chatly.exception.ResourceNotFoundException;
 import com.chatter.Chatly.util.JwtUtil;
 import com.chatter.Chatly.util.MemberContext;
 import io.jsonwebtoken.Claims;
 import lombok.RequiredArgsConstructor;
 import lombok.extern.slf4j.Slf4j;
 import org.springframework.http.server.ServerHttpRequest;
 import org.springframework.http.server.ServerHttpResponse;
 import org.springframework.messaging.Message;
 import org.springframework.messaging.simp.stomp.StompCommand;
 import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
 import org.springframework.messaging.support.ChannelInterceptor;
 import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
 import org.springframework.stereotype.Component;
 import org.springframework.web.socket.WebSocketHandler;
 import org.springframework.web.socket.server.HandshakeInterceptor;

 import java.util.List;
 import java.util.Map;

 @Component
 @RequiredArgsConstructor
 @Slf4j
 public class WebSocketChannelInterceptor implements ChannelInterceptor {

     private final MemberRepository memberRepository;

     public Message<?> preSend(Message<?> message, ChatRoom channel) {
         StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
         // 연결 요청시 JWT 검증
         if (StompCommand.CONNECT.equals(accessor.getCommand())) {
             // Authorization 헤더 추출
             List<String> authorization = accessor.getNativeHeader("Authorization");
             if (authorization != null && !authorization.isEmpty()) {
                 String jwt = authorization.get(0).substring(7); // Bearer ~
                 System.out.println("TESETESTESTES"+jwt);
                 try {
                     Claims claim = JwtUtil.extractClaims(jwt);
                     String memberId = (String) claim.get("member", Map.class).get("id");
                     Member member = memberRepository.findById(memberId)
                             .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + memberId));


//                     // 사용자 인증 정보 설정
//                     CustomUserDetails userDetails = new CustomUserDetails(member);
//                     UsernamePasswordAuthenticationToken authentication =
//                             new UsernamePasswordAuthenticationToken(
//                                     member, null, userDetails.getAuthorities());
//                     SecurityContextHolder.getContext().setAuthentication(authentication);
                 } catch (RuntimeException e) {
                     log.error("JWT Verification Failed: " + e.getMessage());
                     return null;
                 } catch (Exception e) {
                     log.error("An unexpected error occurred: " + e.getMessage());
                     return null;
                 }
             } else {
                 // 클라이언트 측 타임아웃 처리
                 log.error("Authorization header is not found");
                 return null;
             }
         }
         return message;
     }
 }