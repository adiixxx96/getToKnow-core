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
import com.adape.gtk.core.client.beans.UserDTO;
import com.adape.gtk.core.service.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping({ "/user" })
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@PostMapping(value = "/createUser", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDto) {
		return userService.create(userDto);
	}
	
	@PutMapping(value = "/editUser/{id}")
	@ResponseBody
	public ResponseEntity<?> editUser(@PathVariable("id") int id, @Valid @RequestBody UserDTO userDto) {
		return userService.edit(id, userDto);
	}
	
	@DeleteMapping(value = "/deleteUser")
	public ResponseEntity<?> removeUser(@RequestBody List<Integer> id) {
		return userService.delete(id);
	}
	
	@GetMapping(value = "/getUser/{id}")
	public ResponseEntity<?> getUser(@PathVariable("id") int id) {
		return userService.get(id);
	}
	
	@PostMapping(value = "/getUser", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getUsers(@Valid @RequestBody Filter filter) {
		return userService.get(filter);
	}
	
	
}
