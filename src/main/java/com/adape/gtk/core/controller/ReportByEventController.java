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
import com.adape.gtk.core.client.beans.ReportByEventDTO;
import com.adape.gtk.core.service.ReportByEventService;

import jakarta.validation.Valid;


@RestController
@RequestMapping({ "/reportByEvent" })
public class ReportByEventController {
	
	@Autowired
	private ReportByEventService reportByEventService;
	
	@PostMapping(value = "/createReportByEvent", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> createReportByEvent(@Valid @RequestBody ReportByEventDTO reportByEventDto) {
		return reportByEventService.create(reportByEventDto);
	}
	
	@PutMapping(value = "/editReportByEvent/{id}")
	@ResponseBody
	public ResponseEntity<?> editReportByEvent(@PathVariable("id") int id, @Valid @RequestBody ReportByEventDTO reportByEventDto) {
		return reportByEventService.edit(id, reportByEventDto);
	}
	
	@DeleteMapping(value = "/deleteReportByEvent")
	public ResponseEntity<?> removeReportByEvent(@RequestBody List<Integer> id) {
		return reportByEventService.delete(id);
	}
	
	@GetMapping(value = "/getReportByEvent/{id}")
	public ResponseEntity<?> getReportByEvent(@PathVariable("id") int id) {
		return reportByEventService.get(id);
	}
	
	@PostMapping(value = "/getReportsByEvent", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getReportsByEvent(@Valid @RequestBody Filter filter) {
		return reportByEventService.get(filter);
	}
	
	
}
