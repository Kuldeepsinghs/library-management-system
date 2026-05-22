package com.pentagon.library_management.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pentagon.library_management.entity.Category;
import com.pentagon.library_management.repository.CategoryRepo;

@Repository
public class CategoryDao {
	
	@Autowired
	private CategoryRepo category;
	
	
	public Category saveCategory(Category c) {
		return category.save(c);
	}
	
	
	public List<Category> getAllCategory(){
		return category.findAll();
	}
	
	
}
