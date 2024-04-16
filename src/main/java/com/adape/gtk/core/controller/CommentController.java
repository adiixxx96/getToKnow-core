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

import com.adape.gtk.core.client.beans.CommentDTO;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.service.CommentService;

import jakarta.validation.Valid;


@RestController
@RequestMapping({ "/comment" })
public class CommentController {
	
	@Autowired
	private CommentService commentService;
	
	@PostMapping(value = "/createComment", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> createComment(@Valid @RequestBody CommentDTO commentDto) {
		return commentService.create(commentDto);
	}
	
	@PutMapping(value = "/editComment/{id}")
	@ResponseBody
	public ResponseEntity<?> editComment(@PathVariable("id") int id, @Valid @RequestBody CommentDTO commentDto) {
		return commentService.edit(id, commentDto);
	}
	
	@DeleteMapping(value = "/deleteComment")
	public ResponseEntity<?> removeComment(@RequestBody List<Integer> id) {
		return commentService.delete(id);
	}
	
	@GetMapping(value = "/getComment/{id}")
	public ResponseEntity<?> getComment(@PathVariable("id") int id) {
		return commentService.get(id);
	}
	
	@PostMapping(value = "/getComment", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getComments(@Valid @RequestBody Filter filter) {
		return commentService.get(filter);
	}
	
	
}
