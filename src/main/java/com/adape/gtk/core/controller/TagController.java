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
import com.adape.gtk.core.client.beans.TagDTO;
import com.adape.gtk.core.service.TagService;

import jakarta.validation.Valid;


@RestController
@RequestMapping({ "/tag" })
public class TagController {
	
	@Autowired
	private TagService tagService;
	
	@PostMapping(value = "/createTag", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> createTag(@Valid @RequestBody TagDTO tagDto) {
		return tagService.create(tagDto);
	}
	
	@PutMapping(value = "/editTag/{id}")
	@ResponseBody
	public ResponseEntity<?> editTag(@PathVariable("id") int id, @Valid @RequestBody TagDTO tagDto) {
		return tagService.edit(id, tagDto);
	}
	
	@DeleteMapping(value = "/deleteTag")
	public ResponseEntity<?> removeTag(@RequestBody List<Integer> id) {
		return tagService.delete(id);
	}
	
	@GetMapping(value = "/getTag/{id}")
	public ResponseEntity<?> getTag(@PathVariable("id") int id) {
		return tagService.get(id);
	}
	
	@PostMapping(value = "/getTag", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getTags(@Valid @RequestBody Filter filter) {
		return tagService.get(filter);
	}
	
	
}
