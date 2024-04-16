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
import com.adape.gtk.core.client.beans.NotificationDTO;
import com.adape.gtk.core.service.NotificationService;

import jakarta.validation.Valid;


@RestController
@RequestMapping({ "/notification" })
public class NotificationController {
	
	@Autowired
	private NotificationService notificationService;
	
	@PostMapping(value = "/createNotification", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> createNotification(@Valid @RequestBody NotificationDTO notificationDto) {
		return notificationService.create(notificationDto);
	}
	
	@PutMapping(value = "/editNotification/{id}")
	@ResponseBody
	public ResponseEntity<?> editNotification(@PathVariable("id") int id, @Valid @RequestBody NotificationDTO notificationDto) {
		return notificationService.edit(id, notificationDto);
	}
	
	@DeleteMapping(value = "/deleteNotification")
	public ResponseEntity<?> removeNotification(@RequestBody List<Integer> id) {
		return notificationService.delete(id);
	}
	
	@GetMapping(value = "/getNotification/{id}")
	public ResponseEntity<?> getNotification(@PathVariable("id") int id) {
		return notificationService.get(id);
	}
	
	@PostMapping(value = "/getNotification", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getNotifications(@Valid @RequestBody Filter filter) {
		return notificationService.get(filter);
	}
	
	
}
