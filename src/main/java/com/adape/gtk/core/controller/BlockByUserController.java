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

import com.adape.gtk.core.client.beans.BlockByUserDTO;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.service.BlockByUserService;
import jakarta.validation.Valid;


@RestController
@RequestMapping({ "/blockByUser" })
public class BlockByUserController {
	
	@Autowired
	private BlockByUserService blockByUserService;
	
	@PostMapping(value = "/createBlockByUser", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> createBlockByUser(@Valid @RequestBody BlockByUserDTO blockByUserDto) {
		return blockByUserService.create(blockByUserDto);
	}
	
	@PutMapping(value = "/editBlockByUser/{id}")
	@ResponseBody
	public ResponseEntity<?> editBlockByUser(@PathVariable("id") int id, @Valid @RequestBody BlockByUserDTO blockByUserDto) {
		return blockByUserService.edit(id, blockByUserDto);
	}
	
	@DeleteMapping(value = "/deleteBlockByUser")
	public ResponseEntity<?> removeBlockByUser(@RequestBody List<Integer> id) {
		return blockByUserService.delete(id);
	}
	
	@GetMapping(value = "/getBlockByUser/{id}")
	public ResponseEntity<?> getBlockByUser(@PathVariable("id") int id) {
		return blockByUserService.get(id);
	}
	
	@PostMapping(value = "/getBlocksByUser", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getBlocksByUser(@Valid @RequestBody Filter filter) {
		return blockByUserService.get(filter);
	}
	
	
}
