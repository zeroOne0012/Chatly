package com.chatter.Chatly.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.chatter.Chatly.dto.ArticleDto;
import com.chatter.Chatly.dto.ArticleRequestDto;
import com.chatter.Chatly.dto.TargetsDto;
import com.chatter.Chatly.entity.Article;
import com.chatter.Chatly.entity.Channel;
import com.chatter.Chatly.entity.Member;
import com.chatter.Chatly.exception.InvalidRequestException;
import com.chatter.Chatly.exception.ResourceNotFoundException;
import com.chatter.Chatly.exception.SaveFailedException;
import com.chatter.Chatly.repository.ArticleRepository;
import com.chatter.Chatly.repository.ChannelRepository;
import com.chatter.Chatly.repository.MemberRepository;
import com.chatter.Chatly.util.MemberContext;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ChannelRepository channelRepository;
    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final MemberContext memberContext;
    public ArticleService(
        ArticleRepository articleRepository,
        ChannelRepository channelRepository, 
        AuthService authService,
        MemberRepository memberRepository,
        MemberContext memberContext
        ) {
        this.articleRepository = articleRepository;
        this.channelRepository = channelRepository;
        this.authService = authService;
        this.memberRepository = memberRepository;
        this.memberContext = memberContext;
    }
    
    public List<ArticleDto> getAllArticle(Long cid) {
        Channel channel = channelRepository.findById(cid)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + cid));

        return channel.getArticles().stream()
        .map(ArticleDto::from)
        .toList();

        // return articleRepository.findAll().stream()
        // .map(article -> ArticleDto.from(article))
        // .toList();
    }

    // channel 내 특정 member의 모든 article
    public List<ArticleDto> getAllArticleByMember(Long cid) {
        Channel channel = channelRepository.findById(cid)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + cid));

        String mid = memberContext.getMemberIdFromRequest();
        
        return channel.getArticles().stream()
        .filter(article->article.getMember().getId().equals(mid))
        .map(article->ArticleDto.from(article))
        .toList();
    }

    public ArticleDto getArticleById(Long cid, Long id) {
        Article article = articleRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Article not found with ID: " + id));
        if(!Objects.equals(article.getChannel().getId(), cid)){ // 채널 아이디 일치하지 않으면
            throw new InvalidRequestException("Article does not belong to the channel");
        }
        return ArticleDto.from(article);
    }

    public ArticleDto createArticle(Long cid, ArticleRequestDto dto) { // 파일 저장 로직 추가 필요
        if (dto.getTitle()==null || dto.getContent()==null) {
            throw new IllegalArgumentException("Title and content must be provided");
        }
        Article article = dto.toEntity();

        // 헤더에서 유저 정보 가져오기
        String mid = memberContext.getMemberIdFromRequest();
        // article에 Channel, Member 등록
        Channel channel = channelRepository.findById(cid)
        .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + cid));
        Member member = memberRepository.findById(mid)
        .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + mid));
        article.setChannel(channel);
        article.setMember(member);
        // save
        Article savedArticle = articleRepository.save(article);
        if(savedArticle==null) throw new SaveFailedException("Failed to save article");
        return ArticleDto.from(savedArticle);
    }

    public ArticleDto updateArticle(Long cid, Long id, ArticleRequestDto requestDto) {
        Article article = requestDto.toEntity();
        Article target = articleRepository.findById(id)
        .orElseThrow(()-> new ResourceNotFoundException("Article not found with ID: " + id));
        if(!Objects.equals(target.getChannel().getId(), cid)){ // 채널 아이디 일치하지 않으면
            throw new InvalidRequestException("Article does not belong to the channel");
        }
        target.update(article);
        return ArticleDto.from(target);
    }
    
    public void deleteArticle(Long cid, TargetsDto ids) {
        List<Article> articles = articleRepository.findAllByIdIn(ids.getLst());
        if (articles.isEmpty()){
            throw new ResourceNotFoundException("No articles found");
        }
        articles.forEach(article -> {
            if (!Objects.equals(article.getChannel().getId(), cid)) {
                throw new InvalidRequestException("Article does not belong to the channel");
            }
            articleRepository.delete(article);
        });
    }

}
