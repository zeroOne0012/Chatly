package com.chatter.Chatly.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chatter.Chatly.dto.ArticleDto;
import com.chatter.Chatly.dto.ArticleRequestDto;
import com.chatter.Chatly.entity.Article;
import com.chatter.Chatly.entity.Channel;
import com.chatter.Chatly.entity.ChannelMember;
import com.chatter.Chatly.exception.ResourceNotFoundException;
import com.chatter.Chatly.exception.SaveFailedException;
import com.chatter.Chatly.repository.ArticleRepository;
import com.chatter.Chatly.repository.ChannelRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ChannelRepository channelRepository;
    private final AuthService authService;
    private final ChannelMemberService channelMemberService;
    public ArticleService(
        ArticleRepository articleRepository,
        ChannelRepository channelRepository, 
        AuthService authService,
        ChannelMemberService channelMemberService
        ) {
        this.articleRepository = articleRepository;
        this.channelRepository = channelRepository;
        this.authService = authService;
        this.channelMemberService = channelMemberService;
    }
    
    public List<ArticleDto> getAllArticle(Long cid) {
        Channel channel = channelRepository.findById(cid)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with ID: " + cid));

        return channel.getChannelMembers().stream()
        .flatMap(cm -> cm.getArticle().stream())
        .map(article->ArticleDto.from(article))
        .toList();

        // return articleRepository.findAll().stream()
        // .map(article -> ArticleDto.from(article))
        // .toList();
    }

    public ArticleDto getArticleById(Long id) {
        Article article = articleRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Article not found with ID: " + id));
        return ArticleDto.from(article);
    }

    public ArticleDto createArticle(Long cid, ArticleRequestDto dto) { // 파일 저장 로직 추가 필요
        if (dto.getTitle()==null || dto.getContent()==null) {
            throw new IllegalArgumentException("Title and content must be provided");
        }
        Article article = dto.toEntity();

        // 헤더에서 유저 정보 가져오기
        String memberId = authService.getMemberIdFromRequest();
        // 유저 & cid -> ChannelMember 
        ChannelMember channerMember = channelMemberService.isJoined(cid, memberId);
        // article에 ChannelMember 등록
        article.setChannelMember(channerMember);
        // save
        Article savedArticle = articleRepository.save(article);
        if(savedArticle==null) throw new SaveFailedException("Failed to save article");
        return ArticleDto.from(savedArticle);
    }

    public ArticleDto updateArticle(Long id, ArticleRequestDto requestDto) {
        Article article = requestDto.toEntity();
        Article target = articleRepository.findById(id)
        .orElseThrow(()-> new ResourceNotFoundException("Article not found with ID: " + id));
        target.update(article);
        return ArticleDto.from(target);
    }
    
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        articleRepository.delete(article); // 영구 삭제 (Hard Delete)
    }

}
