package com.chatter.Chatly.websocket.message;

import com.chatter.Chatly.domain.article.Article;
import com.chatter.Chatly.domain.attachment.Attachment;
import com.chatter.Chatly.domain.attachment.AttachmentService;
import com.chatter.Chatly.domain.channel.Channel;
import com.chatter.Chatly.domain.channel.ChannelRepository;
import com.chatter.Chatly.domain.channelmember.ChannelMember;
import com.chatter.Chatly.domain.channelmember.ChannelMemberService;
import com.chatter.Chatly.domain.chatroom.ChatRoom;
import com.chatter.Chatly.domain.chatroom.ChatRoomRepository;
import com.chatter.Chatly.domain.member.Member;
import com.chatter.Chatly.domain.member.MemberRepository;
import com.chatter.Chatly.exception.CommonErrorCode;
import com.chatter.Chatly.exception.HttpException;
import com.chatter.Chatly.websocket.message.dto.MessageDto;
import com.chatter.Chatly.websocket.message.dto.MessageRequestDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class MessageService {
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChannelRepository channelRepository;
    private final ChannelMemberService channelMemberService;
    private final AttachmentService attachmentService;

    public Message saveMessage(MessageRequestDto dto, Principal principal){
        String memberId = principal.getName();

        Member member = memberRepository.findById(memberId)
                // .orElseThrow(() -> new HttpException(CommonErrorCode.NOT_FOUND,Member.class,memberId));
                .orElseThrow(() -> new RuntimeException("Not Found at MessageService"));
        ChatRoom chatRoom = chatRoomRepository.findById(dto.getChatRoomId())
                // .orElseThrow(() -> new HttpException(CommonErrorCode.NOT_FOUND,ChatRoom.class,dto.getChatRoomId()));
                .orElseThrow(() -> new RuntimeException("Not Found at MessageService"));

        // 채널(채팅방)에 속함 검사 // 첫 연결 시 검사로 변경할 수 있을 것
        Channel channel = channelRepository.findById(chatRoom.getChannel().getId())
                // .orElseThrow(() ->new HttpException(CommonErrorCode.NOT_FOUND,Channel.class,chatRoom.getChannel().getId()));
                .orElseThrow(() -> new RuntimeException("Not Found at MessageService"));
        ChannelMember cm =  channelMemberService.isJoined(channel.getId(), member.getId());
        if(cm==null){
            log.error("Member does not belongs to the ChatRoom(Channel)");
            throw new RuntimeException("Member does not belongs to the ChatRoom(Channel)");
        }

        Message message = Message.builder()
                .message(dto.getMessage())
                .member(member)
                .chatRoom(chatRoom)
                .likes(0L)
//                .files(dto.getFileUrl()==null ? null : new ArrayList<>(dto.getFileUrl().stream().map(File::getFileUrl).toList()))
                .files(null)
                .build();

        Message saved = messageRepository.save(message);
        if(saved==null) throw new RuntimeException("Message creation failed");

        List<Attachment> attachments = attachmentService.saveFiles("MESSAGE", saved.getId(), dto.getFiles());
        saved.setFiles(attachments);

        return saved;
    }

    public List<MessageDto> getMessages(Long chatRoomId, Long lastMessageId, int size){
        List<Message> messages;

        if (lastMessageId == null) {
            // 가장 최근 메시지부터 size개 가져오기
            messages = messageRepository.findTopByChatRoomIdOrderByIdDesc(chatRoomId, (Pageable) PageRequest.of(0, size));
        } else {
            // lastMessageId보다 작은 (더 오래된) 메시지 중 최신부터 size개
            messages = messageRepository.findByChatRoomIdAndIdLessThanOrderByIdDesc(
                    chatRoomId, lastMessageId, (Pageable) PageRequest.of(0, size));
        }
        return messages.stream()
                .map(MessageDto::from)
                .collect(Collectors.toList());
    }

    public MessageDto updateMessage(Long id, MessageRequestDto dto) {
        Message target = messageRepository.findById(id)
                .orElseThrow(() -> new HttpException(CommonErrorCode.NOT_FOUND, Message.class, id));
        if(!Objects.equals(target.getChatRoom().getId(), dto.getChatRoomId())){
            throw new HttpException(CommonErrorCode.CHATROOM_MESSAGE_NOT_FOUND);
        }
        List<Attachment> attachments = attachmentService.updateFiles("MESSAGE", target.getId(), dto.getFiles(), dto.getRetainedAttachmentIds());

        target.setMessage(dto.getMessage()); // update
        target.getFiles().addAll(attachments);

        return MessageDto.from(target);
    }

    public void deleteMessage(Long chatRoomId, Long id) {
        Message message = messageRepository.findById(id)
            .orElseThrow(()->new HttpException(CommonErrorCode.NOT_FOUND, Message.class, id));
        if(!Objects.equals(message.getChatRoom().getId(), chatRoomId)){
            throw new HttpException(CommonErrorCode.CHATROOM_MESSAGE_NOT_FOUND);
        }
        attachmentService.deleteByEntity("MESSAGE", message.getId());
        messageRepository.delete(message);
    }
}
