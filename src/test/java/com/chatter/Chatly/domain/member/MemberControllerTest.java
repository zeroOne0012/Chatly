package com.chatter.Chatly.domain.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.chatter.Chatly.domain.member.dto.MemberDto;
import com.chatter.Chatly.domain.member.dto.MemberRequestDto;
import com.chatter.Chatly.testUtil.TestEntitySetter;
import com.chatter.Chatly.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // static 없이 @BeforeAll 사용
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberService memberService;

    @Value("${jwt.secret}")
    private String secretKey;
    private Long expiredMs = 1000L * 3600;

    // C: 채널 멤버 X
    // D: 토큰 만료
    // + 로그인 X
    Member memberC; 
    Member memberC_2; // logined, 타인
    Member memberD; 
    // memberE: login X)
    String tokenC; 
    String tokenC_2; 
    String tokenD_expired; 

    String registerReqJson;
    String deleteReqJson;

    /*
     * given
     */
    @BeforeEach
    void setup() throws Exception{
        String pswd = passwordEncoder.encode("ps!");
        memberC = new Member("c", pswd, "nick", "c@");
        memberC_2 = new Member("c_2", pswd, "nick", "c_2@");
        memberD = new Member("d", pswd, "nick", "d@");
        TestEntitySetter.setEntityId(memberC, "createdAt", LocalDateTime.now());
        TestEntitySetter.setEntityId(memberC_2, "createdAt", LocalDateTime.now());
        TestEntitySetter.setEntityId(memberD, "createdAt", LocalDateTime.now());
        tokenC = JwtUtil.createJwt(memberC, secretKey, expiredMs);
        tokenC_2 = JwtUtil.createJwt(memberC_2, secretKey, expiredMs);
        tokenD_expired = JwtUtil.createJwt(memberC, secretKey, 0L);

        MemberRequestDto memberReq = new MemberRequestDto("e", pswd, "nick", "e@");
        registerReqJson = objectMapper.writeValueAsString(memberReq);
    }

    /*
     * when, then
     */
    /////////////////////////////////////////// GET ///////////////////////////////////////////
    private String getAllMembers = "/api/member/all";
    @Test
    void testGetAllMembers_200_개발용_전체유저__로그인_필요() throws Exception{
        when(memberService.getAllMembers()).thenReturn(List.of(memberC, memberD).stream()
            .map(MemberDto::from).toList());
        mockMvc.perform(get(getAllMembers)
            .header("Authorization", "Bearer " + tokenC))
            .andExpect(jsonPath("$.length()").value(2)) // 요소 개수가 2개인지 검사
// ==.andExpect(jsonPath("$", hasSize(2))) // import static org.hamcrest.Matchers.hasSize;

            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("c"))
            .andExpect(jsonPath("$[1].id").value("d"))
            .andExpect(jsonPath("$[0].password").doesNotExist());  // 없는지 검사
// .andExpect(jsonPath("$.someField").exists())        // 있는지 검사
    }
    @Test
    void testGetAllMembers_401_개발용_전체유저__토큰만료() throws Exception{
        when(memberService.getAllMembers()).thenReturn(List.of(memberC, memberD).stream()
            .map(MemberDto::from).toList());
        mockMvc.perform(get(getAllMembers)
            .header("Authorization", "Bearer " + tokenD_expired))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Token expired"));
    }
    @Test
    void testGetAllMembers_403_개발용_로그인X() throws Exception{
        mockMvc.perform(get(getAllMembers))
                .andExpect(status().isForbidden());
    }


    private String getMemberById = "/api/member/d";
    @Test
    void testGetMemberById_200_로그인유저C() throws Exception{
        when(memberService.getMemberById(eq("d"))).thenReturn(MemberDto.from(memberD));
        mockMvc.perform(get(getMemberById)
            .header("Authorization", "Bearer " + tokenC))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("d"))
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(jsonPath("$.nickname").value("nick"))
            .andExpect(jsonPath("$.email").value("d@"));
    }
    @Test
    void testGetMemberById_401_토큰만료D유저() throws Exception{
        when(memberService.getMemberById(eq("d"))).thenReturn(MemberDto.from(memberD));        
        mockMvc.perform(get(getMemberById)
            .header("Authorization", "Bearer " + tokenD_expired))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Token expired"));
    }
    @Test
    void testGetMemberById_403_로그인X() throws Exception{
        mockMvc.perform(get(getMemberById))
                .andExpect(status().isForbidden());
    }

    /////////////////////////////////////////// POST ///////////////////////////////////////////
    private String registerMember = "/api/member/register";
    @Test
    void testRegisterMember_200_회원가입_로그인필요X() throws Exception{
        when(memberService.createMember(
            argThat(dto->
                "e".equals(dto.getId()) && // passwordEncoder.encode("ps!").equals(dto.getPassword()) &&
                "nick".equals(dto.getNickname()) && "e@".equals(dto.getEmail())
            )
        )).thenReturn(MemberDto.from(new Member("e", passwordEncoder.encode("ps!"), "nick", "e@")));

        mockMvc.perform(post(registerMember)
            .contentType(MediaType.APPLICATION_JSON)
            .content(registerReqJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("e"))
            .andExpect(jsonPath("$.nickname").value("nick"))
            .andExpect(jsonPath("$.email").value("e@"))
            .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testRegisterMember_400_회원가입_실패_IDorEmail중복() throws Exception{ // 실패 응답만 확인: id/email 실패 따로 테스트 -> service 테스트에서
        when(memberService.createMember(
            argThat(dto->
                "e".equals(dto.getId()) && // passwordEncoder.encode("ps!").equals(dto.getPassword()) &&
                "nick".equals(dto.getNickname()) && "e@".equals(dto.getEmail())
            )
        )).thenThrow(new IllegalArgumentException("Member data already exists"));

        mockMvc.perform(post(registerMember)
            .contentType(MediaType.APPLICATION_JSON)
            .content(registerReqJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Member data already exists"));
    }

    /////////////////////////////////////////// PUT ///////////////////////////////////////////
    private String updateMember = "/api/member/c_2";
    
    @Test // 실제 본인 필터링 확인 X
    void testUpdateMember_200_성공and403_실패() throws Exception{
        String pswd = passwordEncoder.encode("ps!");

        Member memberC_2 = new Member("c_2", pswd, "updated_nickname", "c_2@");
        TestEntitySetter.setEntityId(memberC_2, "createdAt", LocalDateTime.now());

        MemberRequestDto memberReq = new MemberRequestDto("c_2", pswd, "updated_nickname", "c_2@");
        registerReqJson = objectMapper.writeValueAsString(memberReq);
        
        Member memberC_updated = new Member(memberC.getId(), memberC.getPassword(), memberC.getNickname(), memberC.getEmail());
        memberC_updated.update(memberC_2); // memberC를 C_2(임의의 값)으로 업데이트 시도 -> 아이디, 이메일 유지, 나머지 변경
        
        // 성공
        when(memberService.updateMember( 
            eq("c_2"), argThat(dto->
                "updated_nickname".equals(dto.getNickname()) &&
                "c_2@".equals(dto.getEmail()))
        )).thenReturn(MemberDto.from(memberC_updated));
        mockMvc.perform(put(updateMember)
            .contentType(MediaType.APPLICATION_JSON)
            .content(registerReqJson)
            .header("Authorization", "Bearer " + tokenC))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("c"))
            .andExpect(jsonPath("$.nickname").value("updated_nickname"))
            .andExpect(jsonPath("$.email").value("c@"))
            .andExpect(jsonPath("$.password").doesNotExist());

        // 실패
        memberReq = new MemberRequestDto("c_2", pswd, "differentInput", "c_2@");
        registerReqJson = objectMapper.writeValueAsString(memberReq);
        when(memberService.updateMember( 
            eq("c_2"), argThat(dto->
                "differentInput".equals(dto.getNickname()) &&
                "c_2@".equals(dto.getEmail()))
        )).thenThrow( new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));
        mockMvc.perform(put(updateMember)
            .contentType(MediaType.APPLICATION_JSON)
            .content(registerReqJson)
            .header("Authorization", "Bearer " + tokenC_2))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Forbidden"))
            .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @Test
    void testUpdateMember_401_토큰만료D유저() throws Exception{
        when(memberService.updateMember(any(), any())).thenReturn(MemberDto.from(memberD));        
        mockMvc.perform(put(updateMember)
            .header("Authorization", "Bearer " + tokenD_expired))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Token expired"));
    }
    @Test
    void testUpdateMember_403_로그인X() throws Exception{
        mockMvc.perform(put(updateMember))
                .andExpect(status().isForbidden());
    }

    /////////////////////////////////////////// DELETE ///////////////////////////////////////////
    private String deleteMember = "/api/member/c";
    @Test
    void testDeleteMember_200_본인허용() throws Exception{
        // 성공
        mockMvc.perform(delete(deleteMember)
            .header("Authorization", "Bearer " + tokenC))
            .andExpect(status().isNoContent());
    }
    @Test
    void testDeleteMember_403_타인거부() throws Exception{
        // 실패
        // deleteMember: void
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"))
        .when(memberService).deleteMember(eq("c"));
        mockMvc.perform(delete(deleteMember)
            .header("Authorization", "Bearer " + tokenC_2))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Forbidden"))
            .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @Test
    void testDeleteMember_401_토큰만료D유저() throws Exception{
        mockMvc.perform(delete(deleteMember)
            .header("Authorization", "Bearer " + tokenD_expired))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Token expired"));
    }
    @Test
    void testDeleteMember_403_로그인X() throws Exception{
        mockMvc.perform(delete(deleteMember))
                .andExpect(status().isForbidden());
    }
}
