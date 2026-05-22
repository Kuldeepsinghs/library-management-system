package com.pentagon.library_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pentagon.library_management.entity.Role;
import com.pentagon.library_management.entity.User;
import com.pentagon.library_management.repository.UserRepo;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    public boolean isAdmin(Integer userId) {
        if (userId == null) {
            return false;
        }

        return userRepo.findById(userId)
                .map(User::getRole)
                .filter(Role.ADMIN::equals)
                .isPresent();
    }

    public boolean canAccessUser(Integer loggedInUserId, int targetUserId) {
        return loggedInUserId != null && (loggedInUserId == targetUserId || isAdmin(loggedInUserId));
    }
}
