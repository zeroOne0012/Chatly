package com.chatter.Chatly.domain.article;

import java.util.List;
import java.util.Objects;

import com.chatter.Chatly.domain.attachment.Attachment;
import com.chatter.Chatly.domain.attachment.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.chatter.Chatly.domain.channel.Channel;
import com.chatter.Chatly.domain.channel.ChannelRepository;
import com.chatter.Chatly.domain.member.Member;
import com.chatter.Chatly.domain.member.MemberRepository;
import com.chatter.Chatly.domain.article.dto.ArticleDto;
import com.chatter.Chatly.domain.article.dto.ArticleRequestDto;
import com.chatter.Chatly.domain.common.dto.TargetsDto;
import com.chatter.Chatly.exception.CommonErrorCode;
import com.chatter.Chatly.exception.HttpException;
import com.chatter.Chatly.util.MemberContext;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ChannelRepository channelRepository;
    private final MemberRepository memberRepository;
    private final MemberContext memberContext;
    private final AttachmentService attachmentService;

    
    public List<ArticleDto> getAllArticle(Long cid) {
        Channel channel = channelRepository.findById(cid)
                .orElseThrow(() -> new HttpException(CommonErrorCode.NOT_FOUND, Channel.class, cid));

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
                .orElseThrow(() -> new HttpException(CommonErrorCode.NOT_FOUND, Channel.class, cid));

        String mid = memberContext.getMemberIdFromRequest();
        
        return channel.getArticles().stream()
        .filter(article->article.getMember().getId().equals(mid))
        .map(article->ArticleDto.from(article))
        .toList();
    }

    public ArticleDto getArticleById(Long cid, Long id) {
        Article article = articleRepository.findById(id)
        .orElseThrow(() ->  new HttpException(CommonErrorCode.NOT_FOUND, Article.class, id));

        if(!Objects.equals(article.getChannel().getId(), cid)){ // 채널 아이디 일치하지 않으면
            throw new HttpException(CommonErrorCode.CHANNEL_ARTICLE_NOT_FOUND);
        }
        return ArticleDto.from(article);
    }

    public ArticleDto createArticle(Long cid, ArticleRequestDto dto) { // 파일 저장 로직 추가 필요
        if (dto.getTitle()==null || dto.getContent()==null) {
            throw new HttpException(CommonErrorCode.REQUIRED_FIELD_EMPTY);
        }
        Article article = dto.toEntity();

        // 헤더에서 유저 정보 가져오기
        String mid = memberContext.getMemberIdFromRequest();
        // article에 Channel, Member 등록
        Channel channel = channelRepository.findById(cid)
            .orElseThrow(() -> new HttpException(CommonErrorCode.NOT_FOUND, Channel.class, cid));        
        Member member = memberRepository.findById(mid)
            .orElseThrow(() -> new HttpException(CommonErrorCode.NOT_FOUND, Member.class, mid)); 
        article.setChannel(channel);
        article.setMember(member);


        // save article
        Article savedArticle = articleRepository.save(article);
        if(savedArticle==null) throw new HttpException(CommonErrorCode.SAVE_FAILED, Article.class, "");

        // files
        List<Attachment> attachments = attachmentService.saveFiles("ARTICLE", savedArticle.getId(), dto.getFiles());
        savedArticle.setFiles(attachments);

        return ArticleDto.from(savedArticle);
    }

    public ArticleDto updateArticle(Long cid, Long id, ArticleRequestDto requestDto) {
        Article article = requestDto.toEntity();
        Article target = articleRepository.findById(id)
            .orElseThrow(() -> new HttpException(CommonErrorCode.NOT_FOUND, Article.class, id));
        if(!Objects.equals(target.getChannel().getId(), cid)){ // 채널 아이디 일치하지 않으면
            throw new HttpException(CommonErrorCode.CHANNEL_ARTICLE_NOT_FOUND);
        }

        // 기존 파일 삭제
        attachmentService.deleteByEntity("ARTICLE", target.getId());
        // 새로운 파일 저장
        List<Attachment> attachments = attachmentService.saveFiles("ARTICLE", target.getId(), requestDto.getFiles());

        target.update(article);
        target.setFiles(attachments); // 연관관계 갱신

        return ArticleDto.from(target);
    }
    
    public void deleteArticle(Long cid, TargetsDto ids) {
        List<Article> articles = articleRepository.findAllByIdIn(ids.getLst());
        if (articles.isEmpty()){
            throw new HttpException(CommonErrorCode.NOT_FOUND, Article.class, ids.getLst());
        }
        articles.forEach(article -> {
            if (!Objects.equals(article.getChannel().getId(), cid)) {
                throw new HttpException(CommonErrorCode.CHANNEL_ARTICLE_NOT_FOUND);
            }
            attachmentService.deleteByEntity("ARTICLE", article.getId());
            articleRepository.delete(article);
        });
    }

}
