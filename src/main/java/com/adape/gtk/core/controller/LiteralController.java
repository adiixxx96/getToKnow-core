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
import com.adape.gtk.core.client.beans.LiteralDTO;
import com.adape.gtk.core.service.LiteralService;

import jakarta.validation.Valid;


@RestController
@RequestMapping({ "/literal" })
public class LiteralController {
	
	@Autowired
	private LiteralService literalService;
	
	@PostMapping(value = "/createLiteral", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> createLiteral(@Valid @RequestBody LiteralDTO literalDto) {
		return literalService.create(literalDto);
	}
	
	@PutMapping(value = "/editLiteral/{id}")
	@ResponseBody
	public ResponseEntity<?> editLiteral(@PathVariable("id") int id, @Valid @RequestBody LiteralDTO literalDto) {
		return literalService.edit(id, literalDto);
	}
	
	@DeleteMapping(value = "/deleteLiteral")
	public ResponseEntity<?> removeLiteral(@RequestBody List<Integer> id) {
		return literalService.delete(id);
	}
	
	@GetMapping(value = "/getLiteral/{id}")
	public ResponseEntity<?> getLiteral(@PathVariable("id") int id) {
		return literalService.get(id);
	}
	
	@PostMapping(value = "/getLiteral", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getLiterals(@Valid @RequestBody Filter filter) {
		return literalService.get(filter);
	}
	
	
}
