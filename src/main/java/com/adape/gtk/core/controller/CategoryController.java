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

import com.adape.gtk.core.client.beans.CategoryDTO;
import com.adape.gtk.core.client.beans.Filter;
import com.adape.gtk.core.service.CategoryService;

import jakarta.validation.Valid;


@RestController
@RequestMapping({ "/category" })
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
	@PostMapping(value = "/createCategory", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryDto) {
		return categoryService.create(categoryDto);
	}
	
	@PutMapping(value = "/editCategory/{id}")
	@ResponseBody
	public ResponseEntity<?> editCategory(@PathVariable("id") int id, @Valid @RequestBody CategoryDTO categoryDto) {
		return categoryService.edit(id, categoryDto);
	}
	
	@DeleteMapping(value = "/deleteCategory")
	public ResponseEntity<?> removeCategory(@RequestBody List<Integer> id) {
		return categoryService.delete(id);
	}
	
	@GetMapping(value = "/getCategory/{id}")
	public ResponseEntity<?> getCategory(@PathVariable("id") int id) {
		return categoryService.get(id);
	}
	
	@PostMapping(value = "/getCategory", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getCategories(@Valid @RequestBody Filter filter) {
		return categoryService.get(filter);
	}
	
	
}
