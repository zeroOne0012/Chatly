package com.chatter.Chatly.domain.channelmember;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chatter.Chatly.domain.channel.Channel;
import com.chatter.Chatly.domain.common.Role;
import com.chatter.Chatly.domain.member.Member;
import com.chatter.Chatly.dto.ChannelMemberDto;
import com.chatter.Chatly.dto.ChannelMemberRequestDto;
import com.chatter.Chatly.testUtil.TestEntitySetter;
import com.chatter.Chatly.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
public class ChannelMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ChannelMemberService channelMemberService;

    // A: 관리자
    // B: 채널 멤버
    // C: 채널 멤버 X
    Member memberA;
    Member memberB;
    Member memberC;
    String tokenA;
    String tokenB;
    String tokenC;
    ChannelMember cmA;
    ChannelMember cmB;
    ChannelMember cmC;
    Channel channel;
    Long channelId = 1L;
    String pswd = "pswd";

    @Value("${jwt.secret}")
    private String secretKey;
    private Long expiredMs = 1000L * 3600;

    @BeforeEach
    void setup() throws Exception{
        memberA = new Member("a", pswd, "nickA", "a@");
        memberB = new Member("b", pswd, "nickB", "b@");
        memberC = new Member("c", pswd, "nickC", "c@");
        // DB에 저장 시 createdAt 값 설정하기에 토큰 발급을 위해 임의로 createdAt 값 설정
        TestEntitySetter.setEntityId(memberA, "createdAt", LocalDateTime.now());
        TestEntitySetter.setEntityId(memberB, "createdAt", LocalDateTime.now());
        TestEntitySetter.setEntityId(memberC, "createdAt", LocalDateTime.now());
        tokenA = JwtUtil.createJwt(memberA, secretKey, expiredMs);
        tokenB = JwtUtil.createJwt(memberB, secretKey, expiredMs);
        tokenC = JwtUtil.createJwt(memberC, secretKey, expiredMs);
        channel = new Channel("chnnelName");
        TestEntitySetter.setEntityId(channel, "id", channelId);
        
        cmA = new ChannelMember(channel, memberA);
        cmA.setRole(Role.ADMIN);
        cmB = new ChannelMember(channel, memberB);
        cmC = new ChannelMember(channel, memberC);

        when(channelMemberService.isJoined(channelId, memberA.getId())).thenReturn(cmA);
        when(channelMemberService.isJoined(channelId, memberB.getId())).thenReturn(cmB);
        // C: 채널유저X
    }

    /////////////////////////////////////////// GET ///////////////////////////////////////////
    private String getChannelMembersByChannelId = "/api/channel/channel-member/channel/1";
    @Test
    @DisplayName("1채널 멤버 조회 200")
    void testGetChannelMembersByChannelId_200_채널속한B유저() throws Exception{
        List<ChannelMember> cmLst = new ArrayList<>(List.of(cmA, cmB, cmC));
        List<ChannelMemberDto> cmDtoLst = cmLst.stream()
            .map(ChannelMemberDto::from)
            .toList();
        when(channelMemberService.getChannelMembersByChannelId(eq(channelId))).thenReturn(cmDtoLst);
        mockMvc.perform(get(getChannelMembersByChannelId)
            .header("Authorization", "Bearer "+tokenB))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].channel_id").value(1L));
    }
    @Test
    @DisplayName("1채널 멤버 조회 403") // token만 교체 -> 결과 200-403
    void testGetChannelMembersByChannelId_403_채널속하지않은C유저저() throws Exception{
        List<ChannelMember> cmLst = new ArrayList<>(List.of(cmA, cmB, cmC));
        List<ChannelMemberDto> cmDtoLst = cmLst.stream()
            .map(ChannelMemberDto::from)
            .toList();
        when(channelMemberService.getChannelMembersByChannelId(eq(channelId))).thenReturn(cmDtoLst);
        mockMvc.perform(get(getChannelMembersByChannelId)
            .header("Authorization", "Bearer "+tokenC))
            .andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Access denied"));
    }

    private String getChannelMembersByMemberId = "/api/channel/channel-member/channel/1/member/b";
    @Test
    @DisplayName("b 멤버 속한 채널 모두 조회 200")
    void testGetChannelMembersByMemberId_200_채널속한B유저() throws Exception{
        List<ChannelMember> cmLst = new ArrayList<>(List.of(cmA, cmB, cmC));
        List<ChannelMemberDto> cmDtoLst = cmLst.stream()
            .map(ChannelMemberDto::from)
            .toList();
        when(channelMemberService.getChannelMembersByMemberId(eq("b"))).thenReturn(cmDtoLst);
        mockMvc.perform(get(getChannelMembersByMemberId)
            .header("Authorization", "Bearer "+tokenB))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].channel_id").value(1L));
    }
    @Test
    @DisplayName("b 멤버 속한 채널 모두 조회 403")
    void testGetChannelMembersByMemberId_403_채널속하지않은C유저() throws Exception{
        List<ChannelMember> cmLst = new ArrayList<>(List.of(cmA, cmB, cmC));
        List<ChannelMemberDto> cmDtoLst = cmLst.stream()
            .map(ChannelMemberDto::from)
            .toList();
        when(channelMemberService.getChannelMembersByMemberId(eq("b"))).thenReturn(cmDtoLst);
        mockMvc.perform(get(getChannelMembersByMemberId)
            .header("Authorization", "Bearer "+tokenC))
            .andDo(print())
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Access denied"));
    }

    /////////////////////////////////////////// POST ///////////////////////////////////////////
    private String inviteChannelMembers = "/api/channel/channel-member";
    // insert conlict message !!!!!!
    // path + / !!!!!
    @Test
    void testInviteChannelMembers() throws Exception{
        List<String> invitings = List.of("c"); 
        ChannelMemberRequestDto reqDto = new ChannelMemberRequestDto(1L, invitings);
        String reqJsonString = objectMapper.writeValueAsString(reqDto);
        // System.out.println("JSON 요청 본문: " + reqJsonString);

        List<ChannelMember> cmLst = new ArrayList<>(List.of(cmC));
        List<ChannelMemberDto> cmDtoLst = cmLst.stream()
            .map(ChannelMemberDto::from)
            .toList();
        when(channelMemberService.createChannelMembers(eq(1L), 
            eq(invitings)
        )).thenReturn(cmDtoLst);
        mockMvc.perform(post(inviteChannelMembers)
            .header("Authorization", "Bearer "+tokenB)
            .contentType(MediaType.APPLICATION_JSON)
            .content(reqJsonString))
            .andDo(print())
            .andExpect(status().isOk());
    }


    @Test
    void testChangeMembersRole() {
        // RoleRequestDto
    }

    // @Test
    // void testGetAllChannelMembers() {

    // }




    @Test
    void testKickChannelMember() {

    }
}
