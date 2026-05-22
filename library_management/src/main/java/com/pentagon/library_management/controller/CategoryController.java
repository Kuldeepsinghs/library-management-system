package com.pentagon.library_management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pentagon.library_management.dto.ResponseStructure;
import com.pentagon.library_management.entity.Category;
import com.pentagon.library_management.service.AuthService;
import com.pentagon.library_management.service.CategoryService;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService cs;

    @Autowired
    private AuthService authService;

    
    @PostMapping
    public ResponseEntity<ResponseStructure<Category>>
            saveCategory(@RequestBody Category c, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {

        if (!authService.isAdmin(userId)) {
            return forbidden();
        }
        return cs.saveCategory(c);
    }

    
    @GetMapping
    public ResponseEntity<ResponseStructure<List<Category>>>
            getAllCategory() {

        return cs.getAllCategory();
    }

    private <T> ResponseEntity<ResponseStructure<T>> forbidden() {
        ResponseStructure<T> response = new ResponseStructure<T>();
        response.setStatuscode(HttpStatus.FORBIDDEN.value());
        response.setMessage("Access denied...");
        response.setData(null);
        return new ResponseEntity<ResponseStructure<T>>(response, HttpStatus.FORBIDDEN);
    }
}
