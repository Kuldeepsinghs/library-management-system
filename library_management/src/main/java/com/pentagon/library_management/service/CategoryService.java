package com.pentagon.library_management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pentagon.library_management.dao.CategoryDao;
import com.pentagon.library_management.dto.ResponseStructure;
import com.pentagon.library_management.entity.Category;

@Service
public class CategoryService {

	@Autowired
	private CategoryDao cd;
	
	public ResponseEntity<ResponseStructure<Category>> saveCategory(Category c){
		Category category = cd.saveCategory(c);

        ResponseStructure<Category> response = new ResponseStructure<Category>();

        response.setStatuscode(HttpStatus.CREATED.value());
        response.setMessage("Book Added Successfully...");
        response.setData(category);

        return new ResponseEntity<ResponseStructure<Category>>(response, HttpStatus.CREATED);
	}
	
	public ResponseEntity<ResponseStructure<List<Category>>>getAllCategory() {

		List<Category> categories = cd.getAllCategory();
		
		ResponseStructure<List<Category>> response = new ResponseStructure<List<Category>>();
		
		response.setStatuscode(HttpStatus.OK.value());
		response.setMessage("Categories Fetched Successfully...");
		response.setData(categories);
		
		return new ResponseEntity<ResponseStructure<List<Category>>>(response, HttpStatus.OK);
	}
	
}

