package com.chatter.Chatly.domain.comment;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.chatter.Chatly.domain.article.Article;
import com.chatter.Chatly.domain.article.ArticleRepository;
import com.chatter.Chatly.domain.channel.Channel;
import com.chatter.Chatly.domain.channel.ChannelRepository;
import com.chatter.Chatly.domain.member.Member;
import com.chatter.Chatly.domain.member.MemberRepository;
import com.chatter.Chatly.dto.CommentDto;
import com.chatter.Chatly.dto.CommentRequestDto;
import com.chatter.Chatly.exception.InvalidRequestException;
import com.chatter.Chatly.exception.ResourceNotFoundException;
import com.chatter.Chatly.exception.SaveFailedException;
import com.chatter.Chatly.util.MemberContext;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class CommentService {
    private final int PAGE_SIZE = 20;
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final MemberContext memberContext;
    private final MemberRepository memberRepository;
    public CommentService(
            CommentRepository commentRepository, 
            ArticleRepository articleRepository,
            MemberContext memberContext,
            MemberRepository memberRepository
        ){
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
        this.memberContext = memberContext;
        this.memberRepository = memberRepository;
    }

    // GET - page 단위로 List 반환
    public List<CommentDto> getComments(Long aid, int page) {
        Article article = articleRepository.findById(aid)
        .orElseThrow(() -> new ResourceNotFoundException("Article not found with ID: " + aid));

        Sort sort = Sort.by("id").descending();
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, sort);
        Page<CommentDto> resultPage = commentRepository.findByArticle(article, pageable).map(CommentDto::from);

        return resultPage.getContent();
    }

    // save
    public CommentDto createComment(Long cid, Long aid, CommentRequestDto dto) {
        if (dto.getContent()==null) {
            throw new IllegalArgumentException("content must be provided");
        }
        Comment comment = dto.toEntity();
        
        // 헤더에서 유저 정보 가져오기
        String mid = memberContext.getMemberIdFromRequest();
        // comment에 Article, Member 등록
        Article article = articleRepository.findById(aid)
        .orElseThrow(() -> new ResourceNotFoundException("Article not found with ID: " + aid));
        if(!Objects.equals(cid, article.getChannel().getId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Article does not belong to the channel");
        } // 잘못된 url 요청
        Member member = memberRepository.findById(mid)
        .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + mid));
        comment.setArticle(article);
        comment.setMember(member);

        // save
        Comment savedComment = commentRepository.save(comment);
        if(savedComment==null) throw new SaveFailedException("Failed to save comment");

        return CommentDto.from(comment);
    }


    public CommentDto updateComment(Long cid, Long aid, Long id, CommentRequestDto dto) { // cid, aid exception 처리 필요
        if (!isArticleBelongsToChannel(cid, aid)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Article does not belong to the channel");
        } // 잘못된 url 요청
        Comment comment = dto.toEntity();
        Comment target = commentRepository.findById(id)
        .orElseThrow(()-> new ResourceNotFoundException("Comment not found with ID: " + id));
        
        if (!isCommentBelongsToArticle(aid, target)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment does not belong to the article");
        } // 잘못된 url 요청

        target.update(comment);
        return CommentDto.from(target);
    }

    public void deleteCommet(Long cid, Long aid, Long id) { // cid, aid exception 처리 필요
        if (!isArticleBelongsToChannel(cid, aid)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Article does not belong to the channel");
        } // 잘못된 url 요청
        Comment comment = commentRepository.findById(id)
        .orElseThrow(()-> new ResourceNotFoundException("Comment not found with ID: " + id));
        if (!isCommentBelongsToArticle(aid, comment)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment does not belong to the article");
        } // 잘못된 url 요청

        commentRepository.delete(comment);
    }



    private boolean isArticleBelongsToChannel(Long cid, Long aid){
        Article article = articleRepository.findById(aid)
        .orElseThrow(()-> new ResourceNotFoundException("Article not found with ID: " + aid));
        return Objects.equals(cid, article.getChannel().getId());
    }
    private boolean isCommentBelongsToArticle(Long aid, Comment comment){
        return Objects.equals(aid, comment.getArticle().getId());
    }
}
