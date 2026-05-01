package com.example.demo.controller;

import com.example.demo.entity.Role;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

abstract class SessionControllerSupport {

    protected static long requireLogin(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login is required");
        }

        return (Long) userId;
    }

    protected Role requireUserManager(HttpSession session) {
        requireLogin(session);
        Role role = (Role) session.getAttribute("role");
        if (role != Role.ADMIN && role != Role.SUPER_ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access is required");
        }

        return role;
    }
}
