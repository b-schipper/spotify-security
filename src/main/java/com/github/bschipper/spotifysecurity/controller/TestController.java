package com.github.bschipper.spotifysecurity.controller;

import com.github.bschipper.spotifysecurity.models.User;
import com.github.bschipper.spotifysecurity.repository.UserRepository;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired private UserRepository userRepository;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ARTIST')")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id).get();
    }
}
