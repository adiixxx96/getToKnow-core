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

import com.adape.gtk.core.client.beans.EventDTO;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.service.EventService;

import jakarta.validation.Valid;


@RestController
@RequestMapping({ "/event" })
public class EventController {
	
	@Autowired
	private EventService eventService;
	
	@PostMapping(value = "/createEvent", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> createEvent(@Valid @RequestBody EventDTO eventDto) {
		return eventService.create(eventDto);
	}
	
	@PutMapping(value = "/editEvent/{id}")
	@ResponseBody
	public ResponseEntity<?> editEvent(@PathVariable("id") int id, @Valid @RequestBody EventDTO eventDto) {
		return eventService.edit(id, eventDto);
	}
	
	@DeleteMapping(value = "/deleteEvent")
	public ResponseEntity<?> removeEvent(@RequestBody List<Integer> id) {
		return eventService.delete(id);
	}
	
	@GetMapping(value = "/getEvent/{id}")
	public ResponseEntity<?> getEvent(@PathVariable("id") int id) {
		return eventService.get(id);
	}
	
	@PostMapping(value = "/getEvent", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getEvents(@Valid @RequestBody Filter filter) {
		return eventService.get(filter);
	}
}
