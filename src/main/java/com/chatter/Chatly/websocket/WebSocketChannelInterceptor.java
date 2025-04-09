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
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.http.server.ServerHttpRequest;
 import org.springframework.http.server.ServerHttpResponse;
 import org.springframework.messaging.Message;
 import org.springframework.messaging.MessageChannel;
 import org.springframework.messaging.simp.stomp.StompCommand;
 import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
 import org.springframework.messaging.support.ChannelInterceptor;
 import org.springframework.messaging.support.MessageHeaderAccessor;
 import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
 import org.springframework.security.core.authority.SimpleGrantedAuthority;
 import org.springframework.stereotype.Component;
 import org.springframework.web.socket.WebSocketHandler;
 import org.springframework.web.socket.server.HandshakeInterceptor;

 import java.util.List;
 import java.util.Map;

 @Component
 @RequiredArgsConstructor
 @Slf4j
 public class WebSocketChannelInterceptor implements ChannelInterceptor {

     @Value("${jwt.secret}")
     private String secretKey;

     private final MemberRepository memberRepository;

     public Message<?> preSend(Message<?> message, MessageChannel channel) {
         // StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message); // principal이 null이 됨
         // StompHeaderAccessor를 사용하여 STOMP 메시지 헤더에 접근
         StompHeaderAccessor accessor = MessageHeaderAccessor
                 .getAccessor(message, StompHeaderAccessor.class);

         // 첫 연결 요청시 JWT 검증
         if (StompCommand.CONNECT.equals(accessor.getCommand())) {
             // Authorization 헤더 추출
             List<String> authorization = accessor.getNativeHeader("Authorization");
             if (authorization != null && !authorization.isEmpty()) {
                 String jwt = authorization.get(0).substring(7); // Bearer ~
                 try {
                     Claims claim = JwtUtil.extractClaims(jwt, secretKey);
                     String memberId = (String) claim.get("member", Map.class).get("id");

                     Member member = memberRepository.findById(memberId)
                             .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + memberId));


                     // 사용자 인증 정보 설정
                     UsernamePasswordAuthenticationToken authentication =
                             new UsernamePasswordAuthenticationToken(
                                     memberId, // ← principal: 나중에 getName() 으로 반환됨
                                     null,     // credentials
                                     List.of(new SimpleGrantedAuthority("Member"))      // authorities // 채널 별 권한으로, 전체 권한 설정 X
                             );
//                     SecurityContextHolder.getContext().setAuthentication(authentication);
//                     authentication.setAuthenticated(true); // 넣어줘야 Principal이 세션에 살아 있음 // security에서 막음 //authorities 에 값을 넣어줌으로 해결

                     accessor.setUser(authentication); // principal에서 꺼내 쓸 수 있게 함
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