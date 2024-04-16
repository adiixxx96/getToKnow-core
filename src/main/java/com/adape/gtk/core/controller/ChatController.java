package com.adape.gtk.core.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.adape.gtk.core.client.beans.ChatDTO;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.dao.entity.Chat.ChatId;
import com.adape.gtk.core.service.ChatService;

import jakarta.validation.Valid;


@RestController
@RequestMapping({ "/chat" })
public class ChatController {
	
	@Autowired
	private ChatService chatService;
	
	@PostMapping(value = "/createChat", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> createChat(@Valid @RequestBody ChatDTO chatDto) {
		return chatService.create(chatDto);
	}
	
	@PutMapping(value = "/editChat")
	@ResponseBody
	public ResponseEntity<?> editChat(@Valid @RequestBody ChatDTO chatDto) {
		ChatId id = new ChatId(chatDto.getUser1().getId(), chatDto.getUser2().getId());
		return chatService.edit(id, chatDto);
	}
	
	@DeleteMapping(value = "/deleteChatsObjects")
	public ResponseEntity<?> removeChatsobjects(@Valid @RequestBody List<ChatDTO> chatDtos) {
		
		List<ChatId> list = new ArrayList<ChatId>();
		for(ChatDTO chat : chatDtos) {
			list.add(new ChatId(chat.getUser1().getId(),chat.getUser2().getId()));
		}
			
		return chatService.delete(list);
	}
	
	@DeleteMapping(value = "/deleteChats")
	public ResponseEntity<?> removeChats(@Valid @RequestBody List<ChatId> chatDtos) {
		return chatService.delete(chatDtos);
	}
	
	@GetMapping(value = "/getChat")
	public ResponseEntity<?> getBonusPersonStatus(@Valid @RequestBody ChatId id) {
		return chatService.get(id);
	}
	
	@PostMapping(value = "/getChats", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getBonusesPersonStatus(@Valid @RequestBody Filter filter) {
		return chatService.get(filter);
	}
	
	
}
