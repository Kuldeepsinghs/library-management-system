package com.pentagon.library_management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pentagon.library_management.dao.UserDao;
import com.pentagon.library_management.dto.LoginRequest;
import com.pentagon.library_management.dto.ResponseStructure;
import com.pentagon.library_management.entity.Role;
import com.pentagon.library_management.entity.User;

@Service
public class UserService {
	
	@Autowired
	private UserDao ud;
	
	public ResponseEntity<ResponseStructure<User>> saveUser(User u){
		if (u.getRole() == null) {
			u.setRole(Role.USER);
		}

		User user = ud.saveUser(u);

        ResponseStructure<User> response = new ResponseStructure<User>();

        response.setStatuscode(HttpStatus.CREATED.value());
        response.setMessage("User Added Successfully...");
        response.setData(user);

        return new ResponseEntity<ResponseStructure<User>>(response, HttpStatus.CREATED);
	}

    public ResponseEntity<ResponseStructure<User>> register(User u) {
        ResponseStructure<User> response = new ResponseStructure<User>();

        if (u.getProfile() == null || u.getProfile().getEmail() == null || u.getPassword() == null) {
            response.setStatuscode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Name, email and password are required...");
            response.setData(null);
            return new ResponseEntity<ResponseStructure<User>>(response, HttpStatus.BAD_REQUEST);
        }

        if (ud.getByEmail(u.getProfile().getEmail()) != null) {
            response.setStatuscode(HttpStatus.CONFLICT.value());
            response.setMessage("Email already registered...");
            response.setData(null);
            return new ResponseEntity<ResponseStructure<User>>(response, HttpStatus.CONFLICT);
        }

        u.setRole(ud.hasAdmin() ? Role.USER : Role.ADMIN);
        User user = ud.saveUser(u);

        response.setStatuscode(HttpStatus.CREATED.value());
        response.setMessage(user.getRole() == Role.ADMIN
                ? "Admin account created successfully..."
                : "User registered successfully...");
        response.setData(user);

        return new ResponseEntity<ResponseStructure<User>>(response, HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseStructure<User>> login(LoginRequest request) {
        ResponseStructure<User> response = new ResponseStructure<User>();

        if (request.getEmail() == null || request.getPassword() == null) {
            response.setStatuscode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Email and password are required...");
            response.setData(null);
            return new ResponseEntity<ResponseStructure<User>>(response, HttpStatus.BAD_REQUEST);
        }

        User user = ud.getByEmail(request.getEmail());

        if (user == null || user.getPassword() == null || !user.getPassword().equals(request.getPassword())) {
            response.setStatuscode(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("Invalid email or password...");
            response.setData(null);
            return new ResponseEntity<ResponseStructure<User>>(response, HttpStatus.UNAUTHORIZED);
        }

        response.setStatuscode(HttpStatus.OK.value());
        response.setMessage("Login successful...");
        response.setData(user);

        return new ResponseEntity<ResponseStructure<User>>(response, HttpStatus.OK);
    }

    
    
    public ResponseEntity<ResponseStructure<List<User>>>getAllUser() {

        List<User> users = ud.getAllUser();

        ResponseStructure<List<User>> response = new ResponseStructure<List<User>>();

        response.setStatuscode(HttpStatus.OK.value());
        response.setMessage("Users Fetched Successfully...");
        response.setData(users);

        return new ResponseEntity<ResponseStructure<List<User>>> (response, HttpStatus.OK);
    }

    
    
    public ResponseEntity<ResponseStructure<User>>getById(int id) {

        User user = ud.getById(id);

        ResponseStructure<User> response =
                new ResponseStructure<User>();

        if (user != null) {

            response.setStatuscode(HttpStatus.OK.value());
            response.setMessage("User Found...");
            response.setData(user);

            return new ResponseEntity<ResponseStructure<User>>
                    (response, HttpStatus.OK);

        } else {

            response.setStatuscode(HttpStatus.NOT_FOUND.value());
            response.setMessage("User Not Found...");
            response.setData(null);

            return new ResponseEntity<ResponseStructure<User>>
                    (response, HttpStatus.NOT_FOUND);
        }
    }

    
   
    public ResponseEntity<ResponseStructure<User>>
            updateUser(User u) {

        User existing = ud.getById(u.getId());
        if (existing != null) {
            if (u.getRole() == null) {
                u.setRole(existing.getRole());
            }
            if (u.getPassword() == null || u.getPassword().isBlank()) {
                u.setPassword(existing.getPassword());
            }
        }

        User user = ud.updateUser(u);

        ResponseStructure<User> response =
                new ResponseStructure<User>();

        if (user != null) {

            response.setStatuscode(HttpStatus.OK.value());
            response.setMessage("User Updated Successfully...");
            response.setData(user);

            return new ResponseEntity<ResponseStructure<User>>
                    (response, HttpStatus.OK);

        } else {

            response.setStatuscode(HttpStatus.NOT_FOUND.value());
            response.setMessage("User Not Found...");
            response.setData(null);

            return new ResponseEntity<ResponseStructure<User>>
                    (response, HttpStatus.NOT_FOUND);
        }
    }

    
    
    public ResponseEntity<ResponseStructure<String>>deleteUser(int id) {

        String result = ud.deleteUser(id);

        ResponseStructure<String> response = new ResponseStructure<String>();

        if (result != null) {

            response.setStatuscode(HttpStatus.OK.value());
            response.setMessage("User Deleted Successfully...");
            response.setData(result);

            return new ResponseEntity<ResponseStructure<String>>
                    (response, HttpStatus.OK);

        } else {

            response.setStatuscode(HttpStatus.NOT_FOUND.value());
            response.setMessage("User Not Found...");
            response.setData(null);

            return new ResponseEntity<ResponseStructure<String>>
                    (response, HttpStatus.NOT_FOUND);
        }
    }
    
}
