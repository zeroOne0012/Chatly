package com.chatter.Chatly.domain.article;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.chatter.Chatly.domain.channel.Channel;
import com.chatter.Chatly.domain.channelmember.ChannelMember;
import com.chatter.Chatly.domain.channelmember.ChannelMemberService;
import com.chatter.Chatly.domain.common.Role;
import com.chatter.Chatly.domain.member.Member;
import com.chatter.Chatly.dto.ArticleDto;
import com.chatter.Chatly.dto.ArticleRequestDto;
import com.chatter.Chatly.dto.TargetsDto;
import com.chatter.Chatly.testUtil.TestEntitySetter;
import com.chatter.Chatly.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get; // get 요청
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; // post 요청
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put; // put 요청
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete; // delete 요청
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; // status 검증
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath; // jsonPath 검증
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*; // print



// @WebMvcTest(ArticleController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // static 없이 @BeforeAll 사용
@SpringBootTest
@AutoConfigureMockMvc
// @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS) // 테스트 전 컨텍스트를 새로 고침
// @AutoConfigureTestDatabase // 전체 테스트 시 DB 관련 에러 해결?
// @WebMvcTest
public class ArticleControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // .encode() .matches()
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${jwt.secret}")
    private String secretKey;
    private Long expiredMs = 1000L * 3600;
    
    /*
    @Value("${jwt.token-validity-in-seconds}") 
    private String seconds;
    private Long expiredMs = 1000L * Integer.parseInt(seconds);
    public ArticleControllerTest( @Value("${jwt.token-validity-in-seconds}") String seconds){
        this.expiredMs = 1000L * Integer.parseInt(seconds);
    }
     */

    //@MockBean
    @MockitoBean
    private ArticleService articleService;
    @MockitoBean
    private ChannelMemberService channelMemberService;
    // @MockitoBean
    // private ChannelService channelService;
    // @MockitoBean
    // private MemberService memberService;
    

    // A: 관리자
    // B: 채널 멤버
    // C: 채널 멤버 X
    // D: 토큰 만료
    // + 로그인 X
    Member memberA; 
    Member memberB; 
    Member memberC; 
    Member memberD; 
    // Member memberE;  X
    String tokenA;
    String tokenB; 
    String tokenC; 
    String tokenD; 
    
    Channel channel;
    Long channelId = 1L;

    ChannelMember cmA; // 관리자
    ChannelMember cmB; // 일반 사용자
    // cmC는 없음    
    ChannelMember cmD; // (토큰 만료 사용자)


    String postReqJson;


    // @InjectMocks
    // private ArticleController articleController;

    /*
     * given
     */
    // @BeforeAll
    @BeforeEach
    void setup() throws Exception{
        String pswd = passwordEncoder.encode("a");
        memberA = new Member("a", pswd, "nick", "a@");
        memberB = new Member("b", pswd, "nick", "b@");
        memberC = new Member("c", pswd, "nick", "c@");
        memberD = new Member("d", pswd, "nick", "c@");
        // DB에 저장 시 createdAt 값 설정하기에 토큰 발급을 위해 임의로 createdAt 값 설정
        TestEntitySetter.setEntityId(memberA, "createdAt", LocalDateTime.now());
        TestEntitySetter.setEntityId(memberB, "createdAt", LocalDateTime.now());
        TestEntitySetter.setEntityId(memberC, "createdAt", LocalDateTime.now());
        TestEntitySetter.setEntityId(memberD, "createdAt", LocalDateTime.now());
        tokenA = JwtUtil.createJwt(memberA, secretKey, expiredMs);
        tokenB = JwtUtil.createJwt(memberB, secretKey, expiredMs);
        tokenC = JwtUtil.createJwt(memberC, secretKey, expiredMs);
        tokenD = JwtUtil.createJwt(memberC, secretKey, 0L);
        // mockMvc = MockMvcBuilders.standaloneSetup(articleController).build();
        channel = new Channel("chnnelName");
        TestEntitySetter.setEntityId(channel, "id", channelId);
        
        cmA = new ChannelMember(channel, memberA);
        cmA.setRole(Role.ADMIN);
        cmB = new ChannelMember(channel, memberB);
        cmD = new ChannelMember(channel, memberB);
        // when(channelMemberService.isJoined(channelId, memberA.getId())).thenReturn((ChannelMember) new Object());
        when(channelMemberService.isJoined(channelId, memberA.getId())).thenReturn(cmA);
        when(channelMemberService.isJoined(channelId, memberB.getId())).thenReturn(cmB);
        when(channelMemberService.isJoined(channelId, memberC.getId())).thenReturn(null);
        when(channelMemberService.isJoined(channelId, memberD.getId())).thenReturn(cmD);
    }

    /*
     * when
     */
    // 공통 응답 설정
    // @BeforeAll
    @BeforeEach
    void setResponse () throws Exception{
        // 응답 데이터
        List<ArticleDto> articles = List.of(new ArticleDto(1L, "title", "content", LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT), 1, "id", List.of("url")));
        ArticleDto article = new ArticleDto(1L, "title", "content", LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT), 1, "id", List.of("url"));
        // 요청 데이터
        ArticleRequestDto articleDto = new ArticleRequestDto("req_title", "req_content", null);
        postReqJson = objectMapper.writeValueAsString(articleDto);
        // TargetsDto idxesDto = new TargetsDto(new ArrayList<Long>(Arrays.asList(10L,11L,12L)));

        // "/api/channel/1/article/all"
        when(articleService.getAllArticle(channelId)).thenReturn(articles);

        // "/api/channel/1/article/mine"
        when(articleService.getAllArticleByMember(channelId)).thenReturn(articles);
        
        // "/api/channel/1/article/1(article id)" // 1L: article id
        when(articleService.getArticleById(channelId, 1L)).thenReturn(article);
        
        // "/api/channel/1/article"
        // when(articleService.createArticle(channelId, articleDto)).thenReturn(article); // objectMapper로 들어가는 객체가 새 객체이기에 정상 작동 안함
        // 위 그대로 쓰려면 ArticleRequestDto의 equals overriding 하면 될 것으로 보임
        // when(articleService.createArticle(eq(channelId), any(ArticleRequestDto.class))) // 테스트 통과 O 
        // .thenReturn(article);
        when(articleService.createArticle(eq(channelId), argThat(dto -> // 테스트 통과 O - 나름의 검증까지
            "req_title".equals(dto.getTitle()) && "req_content".equals(dto.getContent())
        ))).thenReturn(article);

        // "/api/channel/1/article/1"
        // when(articleService.updateArticle(channelId, 1L, articleDto)).thenReturn(article);
        // when(articleService.updateArticle(eq(channelId), eq(1L), any(ArticleRequestDto.class)))
        // .thenReturn(article);
        when(articleService.updateArticle(eq(channelId), eq(1L), argThat(dto ->
            "req_title".equals(dto.getTitle()) && "req_content".equals(dto.getContent())
        ))).thenReturn(article);
        
        // "/api/channel/1/article" // article id: int body dto(List)
        // when(articleService.deleteArticle(channelId, idxesDto)).thenReturn(XX);
    
    }


    /*
     * then
     */
    /////////////////////////////////////////// GET ///////////////////////////////////////////
    // "/api/channel/1/article/all"
    private String getAllArticle = "/api/channel/1/article/all";

    @Test
    void testGetAllArticle_200_채널속한B유저() throws Exception{
        mockMvc.perform(get(getAllArticle)
                .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("title"))
                .andExpect(jsonPath("$[0].content").value("content"))
                .andExpect(jsonPath("$[0].created_at").value(LocalDate.now().toString().concat("T00:00:00")))
                .andExpect(jsonPath("$[0].like_count").value(1))
                .andExpect(jsonPath("$[0].member_id").value("id"))
                .andExpect(jsonPath("$[0].file_urls[0]").value("url"));
    }

    @Test
    void testGetAllArticle_403_채널속하지않은C유저() throws Exception{
        mockMvc.perform(get(getAllArticle)
                .header("Authorization", "Bearer " + tokenC))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access denied"));
    }

    @Test
    void testGetAllArticle_401_토큰만료D유저() throws Exception{
        mockMvc.perform(get(getAllArticle)
                .header("Authorization", "Bearer " + tokenD))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token expired"));
    }
    @Test
    void testGetAllArticle_403_로그인X() throws Exception{
        mockMvc.perform(get(getAllArticle))
                .andExpect(status().isForbidden());
    }





    // "/api/channel/1/article/mine"
    private String getAllArticleByMember = "/api/channel/1/article/mine";

    @Test
    void testGetAllArticleByMember_200_채널속한B유저() throws Exception{
        mockMvc.perform(get(getAllArticleByMember)
                .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("title"))
                .andExpect(jsonPath("$[0].content").value("content"))
                .andExpect(jsonPath("$[0].created_at").value(LocalDate.now().toString().concat("T00:00:00")))
                .andExpect(jsonPath("$[0].like_count").value(1))
                .andExpect(jsonPath("$[0].member_id").value("id"))
                .andExpect(jsonPath("$[0].file_urls[0]").value("url"));
    }

    @Test
    void testGetAllArticleByMember_403_채널속하지않은C유저() throws Exception{
        mockMvc.perform(get(getAllArticleByMember)
                .header("Authorization", "Bearer " + tokenC))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access denied"));
    }

    @Test
    void testGetAllArticleByMember_401_토큰만료D유저() throws Exception{
        mockMvc.perform(get(getAllArticleByMember)
                .header("Authorization", "Bearer " + tokenD))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token expired"));
    }
    @Test
    void testGetAllArticleByMember_403_로그인X() throws Exception{
        mockMvc.perform(get(getAllArticleByMember))
                .andExpect(status().isForbidden());
    }


    
    // "/api/channel/1/article/1(article id)" // 1L: article id
    private String getArticleById = "/api/channel/1/article/1";
    @Test
    void testGetArticleById_200_채널속한B유저() throws Exception{
        mockMvc.perform(get(getArticleById)
                .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.created_at").value(LocalDate.now().toString().concat("T00:00:00")))
                .andExpect(jsonPath("$.like_count").value(1))
                .andExpect(jsonPath("$.member_id").value("id"))
                .andExpect(jsonPath("$.file_urls[0]").value("url"));
    }

    @Test
    void testGetArticleById_403_채널속하지않은C유저() throws Exception{
        mockMvc.perform(get(getArticleById)
                .header("Authorization", "Bearer " + tokenC))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access denied"));
    }

    @Test
    void testGetArticleById_401_토큰만료D유저() throws Exception{
        mockMvc.perform(get(getArticleById)
                .header("Authorization", "Bearer " + tokenD))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token expired"));
    }
    @Test
    void testGetArticleById_403_로그인X() throws Exception{
        mockMvc.perform(get(getArticleById))
                .andExpect(status().isForbidden());
    }

    /////////////////////////////////////////// POST ///////////////////////////////////////////
    // "/api/channel/1/article"
    private String createArticle = "/api/channel/1/article";
    @Test
    void testCreateArticle_200_채널속한B유저() throws Exception{
        mockMvc.perform(post(createArticle)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postReqJson)
                .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.created_at").value(LocalDate.now().toString().concat("T00:00:00")))
                .andExpect(jsonPath("$.like_count").value(1))
                .andExpect(jsonPath("$.member_id").value("id"))
                .andExpect(jsonPath("$.file_urls[0]").value("url"));
    }

    @Test
    void testCreateArticle_403_채널속하지않은C유저() throws Exception{
        mockMvc.perform(post(createArticle)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postReqJson)
                .header("Authorization", "Bearer " + tokenC))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access denied"));
    }

    @Test
    void testCreateArticle_401_토큰만료D유저() throws Exception{
        mockMvc.perform(post(createArticle)
                .header("Authorization", "Bearer " + tokenD))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token expired"));
    }
    @Test
    void testCreateArticle_403_로그인X() throws Exception{
        mockMvc.perform(post(createArticle))
                .andExpect(status().isForbidden());
    }

    /////////////////////////////////////////// PUT ///////////////////////////////////////////
    // "/api/channel/1/article/1"
    private String updateArticle = "/api/channel/1/article/1";
    // @Test
    // void testUpdateArticle_200_채널속한B유저() throws Exception{
    //     mockMvc.perform(put(updateArticle)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(postReqJson)
    //             .header("Authorization", "Bearer " + tokenB))
    //             .andDo(print()) // 응답 콘솔 출력
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.title").value("title"))
    //             .andExpect(jsonPath("$.content").value("content"))
    //             .andExpect(jsonPath("$.created_at").value(LocalDate.now().toString().concat("T00:00:00")))
    //             .andExpect(jsonPath("$.like_count").value(1))
    //             .andExpect(jsonPath("$.member_id").value("id"))
    //             .andExpect(jsonPath("$.file_urls[0]").value("url"));
    // }

    // @Test
    // void testUpdateArticle_403_채널속하지않은C유저() throws Exception{
    //     mockMvc.perform(put(updateArticle)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(postReqJson)
    //             .header("Authorization", "Bearer " + tokenC))
    //             .andExpect(status().isForbidden())
    //             .andExpect(jsonPath("$.error").value("Access denied"));
    // }
    //////////// ? RequireCheckAspect 에서 paramNames가 null이 되어버려 테스트 불가 (실제 동작, 테스트만 안됨)
    //////////// ! paramNames는 없지만 args는 있음, aop 로직을 paramName이 아닌 args index 기반으로 바꾸면 테스트도 가능할 것으로 보임!

    @Test
    void testUpdateArticle_401_토큰만료D유저() throws Exception{
        mockMvc.perform(put(updateArticle)
                .header("Authorization", "Bearer " + tokenD))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token expired"));
    }
    @Test
    void testUpdateArticle_403_로그인X() throws Exception{
        mockMvc.perform(put(updateArticle))
                .andExpect(status().isForbidden());
    }

    /////////////////////////////////////////// DELETE ///////////////////////////////////////////
    // "/api/channel/1/article" // article id: int body dto(List)
    private String deleteArticle = "/api/channel/1/article";
/////    @Test
/////    void testDeleteArticle_200_채널속한B유저() throws Exception{
/////        mockMvc.perform(delete(deleteArticle)
/////                .contentType(MediaType.APPLICATION_JSON)
/////                .content(postReqJson)
/////                .header("Authorization", "Bearer " + tokenB))
/////                .andDo(print())
/////                .andExpect(status().isNoContent());
/////    }

/////    @Test
/////    void testDeleteArticle_403_채널속하지않은C유저() throws Exception{
/////        mockMvc.perform(delete(deleteArticle)
/////                .contentType(MediaType.APPLICATION_JSON)
/////                .content(postReqJson)
/////                .header("Authorization", "Bearer " + tokenC))
/////                .andExpect(status().isForbidden())
/////                .andExpect(jsonPath("$.error").value("Access denied"));
/////    }
    ////////// ? RequireCheckAspect 에서 paramNames가 null이 되어버려 테스트 불가 (실제 동작, 테스트만 안됨)

    @Test
    void testDeleteArticle_401_토큰만료D유저() throws Exception{
        mockMvc.perform(delete(deleteArticle)
                .header("Authorization", "Bearer " + tokenD))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token expired"));
    }
    @Test
    void testDeleteArticle_403_로그인X() throws Exception{
        mockMvc.perform(delete(deleteArticle))
                .andExpect(status().isForbidden());
    }
}

// @Configuration
// @EnableAspectJAutoProxy(proxyTargetClass = true)
// public class TestAopConfig {}