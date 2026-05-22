package com.pentagon.library_management.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pentagon.library_management.entity.Role;
import com.pentagon.library_management.entity.User;
import com.pentagon.library_management.repository.UserRepo;

@Repository
public class UserDao {

	@Autowired
	private UserRepo user;
	
	public User saveUser(User u) {
		return user.save(u);
	}

	public long countUsers() {
		return user.count();
	}

	public User getByEmail(String email) {
		return user.findByProfileEmail(email).orElse(null);
	}

	public boolean hasAdmin() {
		return user.existsByRole(Role.ADMIN);
	}
	
	
	public List<User> getAllUser(){
		return user.findAll();
	}
	
	
	public User getById(int id) {
		
		Optional <User> o = user.findById(id);
		
		if(o.isPresent()) {
			return o.get();
			
		}else {
			return null;
		}
	}
	
	public User updateUser(User u) {
		
		int id = u.getId();
		Optional<User> o = user.findById(id);
		
		if(o.isPresent()) {
			return user.save(u);
			
		}else {
			return null;
		}
	}
	
	public String deleteUser(int id) {
		
		Optional<User> o = user.findById(id);
		
		if(o.isPresent()) {
			User u = o.get();
			user.delete(u);
			return "user deleted";
			
		}else {
			return null;
		}
		
		
	}
}
