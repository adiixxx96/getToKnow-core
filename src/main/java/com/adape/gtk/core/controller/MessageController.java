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

import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.client.beans.MessageDTO;
import com.adape.gtk.core.service.MessageService;

import jakarta.validation.Valid;


@RestController
@RequestMapping({ "/message" })
public class MessageController {
	
	@Autowired
	private MessageService messageService;
	
	@PostMapping(value = "/createMessage", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> createMessage(@Valid @RequestBody MessageDTO messageDto) {
		return messageService.create(messageDto);
	}
	
	@PutMapping(value = "/editMessage/{id}")
	@ResponseBody
	public ResponseEntity<?> editMessage(@PathVariable("id") int id, @Valid @RequestBody MessageDTO messageDto) {
		return messageService.edit(id, messageDto);
	}
	
	@DeleteMapping(value = "/deleteMessage")
	public ResponseEntity<?> removeMessage(@RequestBody List<Integer> id) {
		return messageService.delete(id);
	}
	
	@GetMapping(value = "/getMessage/{id}")
	public ResponseEntity<?> getMessage(@PathVariable("id") int id) {
		return messageService.get(id);
	}
	
	@PostMapping(value = "/getMessage", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getMessages(@Valid @RequestBody Filter filter) {
		return messageService.get(filter);
	}
	
	
}
