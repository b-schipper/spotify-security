package com.github.bschipper.spotifysecurity.features.user;

import com.github.bschipper.spotifysecurity.features.user.response.ProfileResponse;
import com.github.bschipper.spotifysecurity.security.CurrentUser;
import com.github.bschipper.spotifysecurity.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProfileResponse> getUserProfile(@CurrentUser UserPrincipal userPrincipal) {
        return ResponseEntity.ok(userService.getUserProfile(userPrincipal.getId()));
    }
}
