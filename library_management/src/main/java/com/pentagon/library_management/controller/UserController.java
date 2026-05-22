package com.pentagon.library_management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pentagon.library_management.dto.LoginRequest;
import com.pentagon.library_management.dto.ResponseStructure;
import com.pentagon.library_management.entity.User;
import com.pentagon.library_management.service.AuthService;
import com.pentagon.library_management.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService us;

    @Autowired
    private AuthService authService;


    @PostMapping
    public ResponseEntity<ResponseStructure<User>>
            saveUser(@RequestBody User u, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {

        if (!authService.isAdmin(userId)) {
            return forbidden();
        }
        return us.saveUser(u);
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseStructure<User>>
            register(@RequestBody User u) {

        return us.register(u);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseStructure<User>>
            login(@RequestBody LoginRequest request) {

        return us.login(request);
    }

    
    @GetMapping
    public ResponseEntity<ResponseStructure<List<User>>>
            getAllUser(@RequestHeader(value = "X-User-Id", required = false) Integer userId) {

        if (!authService.isAdmin(userId)) {
            return forbidden();
        }
        return us.getAllUser();
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<User>>
            getById(@PathVariable int id, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {

        if (!authService.canAccessUser(userId, id)) {
            return forbidden();
        }
        return us.getById(id);
    }

    
    @PutMapping
    public ResponseEntity<ResponseStructure<User>>
            updateUser(@RequestBody User u, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {

        if (!authService.canAccessUser(userId, u.getId())) {
            return forbidden();
        }
        return us.updateUser(u);
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseStructure<String>>
            deleteUser(@PathVariable int id, @RequestHeader(value = "X-User-Id", required = false) Integer userId) {

        if (!authService.isAdmin(userId)) {
            return forbidden();
        }
        return us.deleteUser(id);
    }

    private <T> ResponseEntity<ResponseStructure<T>> forbidden() {
        ResponseStructure<T> response = new ResponseStructure<T>();
        response.setStatuscode(HttpStatus.FORBIDDEN.value());
        response.setMessage("Access denied...");
        response.setData(null);
        return new ResponseEntity<ResponseStructure<T>>(response, HttpStatus.FORBIDDEN);
    }
}
