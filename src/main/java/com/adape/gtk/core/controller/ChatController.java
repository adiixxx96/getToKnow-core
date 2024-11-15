package com.adape.gtk.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.adape.gtk.core.client.beans.ChatDTO;
import com.adape.gtk.core.client.beans.Filter;
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
	
	@PutMapping(value = "/editChat/{id}")
	@ResponseBody
	public ResponseEntity<?> editChat(@PathVariable("id") int id, @Valid @RequestBody ChatDTO chatDto) {
		return chatService.edit(id, chatDto);
	}
	
	@DeleteMapping(value = "/deleteChats")
	public ResponseEntity<?> removeChats(@Valid @RequestBody List<Integer> id) {
		return chatService.delete(id);
	}
	
	@GetMapping(value = "/getChat/{id}")
	public ResponseEntity<?> getChat(@PathVariable("id") int id) {
		return chatService.get(id);
	}
	
	@PostMapping(value = "/getChats", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getChats(@Valid @RequestBody Filter filter) {
		return chatService.get(filter);
	}
	
	
}
