package com.chatter.Chatly.domain.channel;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatter.Chatly.annotation.RequirePrivilege;
import com.chatter.Chatly.dto.ChannelDto;
import com.chatter.Chatly.dto.ChannelRequestDto;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/channel")
public class ChannelController {
    private final ChannelService channelService;
    public ChannelController(ChannelService channelService){
        this.channelService = channelService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ChannelDto>> getAllChannel() {
        List<ChannelDto> channels = channelService.getAllChannel();
        return ResponseEntity.ok(channels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChannelDto> getChannelById(@PathVariable("id") Long id) {
        ChannelDto channel = channelService.getChannelById(id);
        return ResponseEntity.ok(channel);
    }

    @PostMapping
    public ResponseEntity<ChannelDto> createChannel(@RequestBody ChannelRequestDto dto) {
        ChannelDto channel = channelService.createChannel(dto);
        return ResponseEntity.ok(channel);
    }

    @PutMapping("/{id}")
    @RequirePrivilege
    // @RequireOwnership(entityClass = Channel.class, idParam = "id")
    public ResponseEntity<ChannelDto> updateChannel(@PathVariable("id") Long id, @RequestBody ChannelRequestDto dto) {
        ChannelDto channel = channelService.updateChannel(id, dto);
        return ResponseEntity.ok(channel);

    }
    @DeleteMapping("/{id}")
    @RequirePrivilege
    // @RequireOwnership(entityClass = Channel.class, idParam = "id")
    public ResponseEntity<ChannelDto> deleteChannel(@PathVariable("id") Long id) {
        channelService.deleteChannel(id);
        return ResponseEntity.noContent().build();
    }

    // profile patch
}
