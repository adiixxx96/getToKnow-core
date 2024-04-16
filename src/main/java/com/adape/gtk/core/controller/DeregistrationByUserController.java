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

import com.adape.gtk.core.client.beans.DeregistrationByUserDTO;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.service.DeregistrationByUserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping({ "/deregistrationByUser" })
public class DeregistrationByUserController {
	
	@Autowired
	private DeregistrationByUserService deregistrationByUserService;
	
	@PostMapping(value = "/createDeregistrationByUser", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> createDeregistrationByUser(@Valid @RequestBody DeregistrationByUserDTO deregistrationByUserDto) {
		return deregistrationByUserService.create(deregistrationByUserDto);
	}
	
	@PutMapping(value = "/editDeregistrationByUser/{id}")
	@ResponseBody
	public ResponseEntity<?> editDeregistrationByUser(@PathVariable("id") int id, @Valid @RequestBody DeregistrationByUserDTO deregistrationByUserDto) {
		return deregistrationByUserService.edit(id, deregistrationByUserDto);
	}
	
	@DeleteMapping(value = "/deleteDeregistrationByUser")
	public ResponseEntity<?> removeDeregistrationByUser(@RequestBody List<Integer> id) {
		return deregistrationByUserService.delete(id);
	}
	
	@GetMapping(value = "/getDeregistrationByUser/{id}")
	public ResponseEntity<?> getDeregistrationByUser(@PathVariable("id") int id) {
		return deregistrationByUserService.get(id);
	}
	
	@PostMapping(value = "/getDeregistrationsByUser", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getDeregistrationsByUser(@Valid @RequestBody Filter filter) {
		return deregistrationByUserService.get(filter);
	}
	
	
}
