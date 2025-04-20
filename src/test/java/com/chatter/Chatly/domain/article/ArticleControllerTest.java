package com.chatter.Chatly.domain.article;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
import com.chatter.Chatly.domain.article.dto.ArticleDto;
import com.chatter.Chatly.domain.article.dto.ArticleRequestDto;
import com.chatter.Chatly.domain.common.dto.TargetsDto;
import com.chatter.Chatly.testUtil.TestEntitySetter;
import com.chatter.Chatly.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;

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
    @MockitoBean
    private EntityManager entityManager;
   

    // A: 관리자
    // B: 채널 멤버
    // C: 채널 멤버 X
    // D: 토큰 만료
    // + 로그인 X
    Member memberA;     // RequireOwnership 테스트: 관리자(수정 가능)
    Member memberB;     // RequireOwnership 테스트: 본인  (수정 가능)
    Member memberB_2;   // RequireOwnership 테스트: 타인  (수정 불가)
    Member memberC; 
    Member memberD; 
    // Member memberE;  X
    String tokenA;
    String tokenB; 
    String tokenB_2; 
    String tokenC; 
    String tokenD; 
    
    Channel channel;
    Long channelId = 1L;

    ChannelMember cmA; // 관리자
    ChannelMember cmB; // 일반 사용자
    ChannelMember cmB_2; // 일반 사용자
    // cmC는 없음    
    ChannelMember cmD; // (토큰 만료 사용자)


    String postReqJson;
    String idxesReqJson;
    // Class<?> entityClass; // for aop

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
        memberB_2 = new Member("b_2", pswd, "nick", "b_2@");
        memberC = new Member("c", pswd, "nick", "c@");
        memberD = new Member("d", pswd, "nick", "c@");
        // DB에 저장 시 createdAt 값 설정하기에 토큰 발급을 위해 임의로 createdAt 값 설정
        TestEntitySetter.setEntityId(memberA, "createdAt", LocalDateTime.now());
        TestEntitySetter.setEntityId(memberB, "createdAt", LocalDateTime.now());
        TestEntitySetter.setEntityId(memberB_2, "createdAt", LocalDateTime.now());
        TestEntitySetter.setEntityId(memberC, "createdAt", LocalDateTime.now());
        TestEntitySetter.setEntityId(memberD, "createdAt", LocalDateTime.now());
        tokenA = JwtUtil.createJwt(memberA, secretKey, expiredMs);
        tokenB = JwtUtil.createJwt(memberB, secretKey, expiredMs);
        tokenB_2 = JwtUtil.createJwt(memberB_2, secretKey, expiredMs);
        tokenC = JwtUtil.createJwt(memberC, secretKey, expiredMs);
        tokenD = JwtUtil.createJwt(memberC, secretKey, 0L);
        // mockMvc = MockMvcBuilders.standaloneSetup(articleController).build();
        channel = new Channel("chnnelName");
        TestEntitySetter.setEntityId(channel, "id", channelId);
        
        cmA = new ChannelMember(channel, memberA);
        cmA.setRole(Role.ADMIN);
        cmB = new ChannelMember(channel, memberB);
        cmB_2 = new ChannelMember(channel, memberB_2);
        cmD = new ChannelMember(channel, memberD);
        // when(channelMemberService.isJoined(channelId, memberA.getId())).thenReturn((ChannelMember) new Object());
        when(channelMemberService.isJoined(channelId, memberA.getId())).thenReturn(cmA);
        when(channelMemberService.isJoined(channelId, memberB.getId())).thenReturn(cmB);
        when(channelMemberService.isJoined(channelId, memberB_2.getId())).thenReturn(cmB_2);
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
        List<ArticleDto> articles = List.of(new ArticleDto(1L, "title", "content", LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT), 1, "id", 0, List.of("url")));
        ArticleDto article = new ArticleDto(1L, "title", "content", LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT), 1, "id", 0, List.of("url"));
        // 요청 데이터
        ArticleRequestDto articleDto = new ArticleRequestDto("req_title", "req_content", null);
        postReqJson = objectMapper.writeValueAsString(articleDto);
        TargetsDto idxesDto = new TargetsDto(new ArrayList<Long>(Arrays.asList(10L,11L,12L)));
        idxesReqJson = objectMapper.writeValueAsString(idxesDto);

        // GET
        // "/api/channel/1/article/all"
        when(articleService.getAllArticle(channelId)).thenReturn(articles);
        
        // GET
        // "/api/channel/1/article/mine"
        when(articleService.getAllArticleByMember(channelId)).thenReturn(articles);
        
        // GET
        // "/api/channel/1/article/1(article id)" // 1L: article id
        when(articleService.getArticleById(channelId, 1L)).thenReturn(article);
        
        // POST
        // "/api/channel/1/article"
        // when(articleService.createArticle(channelId, articleDto)).thenReturn(article); // objectMapper로 들어가는 객체가 새 객체이기에 정상 작동 안함
        // 위 그대로 쓰려면 ArticleRequestDto의 equals overriding 하면 될 것으로 보임
        // when(articleService.createArticle(eq(channelId), any(ArticleRequestDto.class))) // 테스트 통과 O 
        // .thenReturn(article);
        when(articleService.createArticle(eq(channelId), argThat(dto -> // 테스트 통과 O - 나름의 검증까지
            "req_title".equals(dto.getTitle()) && "req_content".equals(dto.getContent())
        ))).thenReturn(article);

        // PUT
        // "/api/channel/1/article/1"
        when(articleService.updateArticle(eq(channelId), eq(1L), argThat(dto ->
            "req_title".equals(dto.getTitle()) && "req_content".equals(dto.getContent())
        ))).thenReturn(article);

        // for aop: PUT, DELETE - RequireOwnership test!
        Article targetArticle = new Article("",""); // 인증에만 쓰일 article: 실제론 수정의 대상
        targetArticle.setMember(memberB); // article 편집 - 본인인지 테스트에서 B가 본인(수정 가능), B_2가 타인(수정 불가)
        // when(entityManager.find(any(), any())).thenReturn(targetArticle);
        when(entityManager.find(eq(Article.class), eq(1L))).thenReturn(targetArticle);
        
        // DELETE
        // "/api/channel/1/article" // article id: int body dto(List)
        // when(articleService.deleteArticle(channelId, idxesDto)).thenReturn(XX);
        when(entityManager.find(eq(Article.class), eq(10L))).thenReturn(targetArticle); // 지울 목록, 인증에만 쓰이기에 targetArticle 상관 X
        when(entityManager.find(eq(Article.class), eq(11L))).thenReturn(targetArticle);
        when(entityManager.find(eq(Article.class), eq(12L))).thenReturn(targetArticle);
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
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Access denied"));
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
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Access denied"));
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
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Access denied"));
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
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Access denied"));
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
    @Test
    void testUpdateArticle_200_채널속한B유저() throws Exception{
        mockMvc.perform(put(updateArticle)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postReqJson)
                .header("Authorization", "Bearer " + tokenB))
                .andDo(print()) // 응답 콘솔 출력
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.created_at").value(LocalDate.now().toString().concat("T00:00:00")))
                .andExpect(jsonPath("$.like_count").value(1))
                .andExpect(jsonPath("$.member_id").value("id"))
                .andExpect(jsonPath("$.file_urls[0]").value("url"));
    }
    @Test
    void testUpdateArticle_403_채널속한B유저_타인() throws Exception{
        mockMvc.perform(put(updateArticle)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postReqJson)
                .header("Authorization", "Bearer " + tokenB_2))
                .andDo(print()) // 응답 콘솔 출력
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @Test
    void testUpdateArticle_403_채널속하지않은C유저() throws Exception{
        mockMvc.perform(put(updateArticle)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postReqJson)
                .header("Authorization", "Bearer " + tokenC))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Access denied"));
    }


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
    @Test
    void testDeleteArticle_200_채널속한B유저() throws Exception{
        mockMvc.perform(delete(deleteArticle)
                .contentType(MediaType.APPLICATION_JSON)
                .content(idxesReqJson)
                .header("Authorization", "Bearer " + tokenB))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
    @Test
    void testDeleteArticle_200_채널속한B유저_타인() throws Exception{
        mockMvc.perform(delete(deleteArticle)
                .contentType(MediaType.APPLICATION_JSON)
                .content(idxesReqJson)
                .header("Authorization", "Bearer " + tokenB_2))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @Test
    void testDeleteArticle_403_채널속하지않은C유저() throws Exception{
        mockMvc.perform(delete(deleteArticle)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postReqJson)
                .header("Authorization", "Bearer " + tokenC))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

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