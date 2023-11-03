package com.github.bschipper.spotifysecurity.controller;

import com.github.bschipper.spotifysecurity.controller.payload.request.TokenRefreshRequest;
import com.github.bschipper.spotifysecurity.controller.payload.response.TokenRefreshResponse;
import com.github.bschipper.spotifysecurity.models.ERole;
import com.github.bschipper.spotifysecurity.security.services.AuthenticationService;
import com.github.bschipper.spotifysecurity.controller.payload.request.SigninRequest;
import com.github.bschipper.spotifysecurity.controller.payload.request.SignupRequest;
import com.github.bschipper.spotifysecurity.controller.payload.response.JwtResponse;
import com.github.bschipper.spotifysecurity.controller.payload.response.MessageResponse;
import com.github.bschipper.spotifysecurity.security.CurrentUser;
import com.github.bschipper.spotifysecurity.security.UserPrincipal;
import com.github.bschipper.spotifysecurity.security.services.RefreshTokenService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody SigninRequest request) {
        return ResponseEntity.ok(authenticationService.authenticateUser(
                request.getUsername(), request.getPassword()));
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(201).body(authenticationService.registerUser(
                request.getUsername(), request.getEmail(), request.getPassword(), ERole.ROLE_USER));
    }

    @PostMapping("/signup/artist")
    public ResponseEntity<MessageResponse> registerArtist(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(201).body(authenticationService.registerUser(
                request.getUsername(), request.getEmail(), request.getPassword(), ERole.ROLE_ARTIST));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(refreshTokenService.findByToken(request.getRefreshToken()));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(@CurrentUser UserPrincipal userPrincipal) {
        refreshTokenService.deleteByUserId(userPrincipal.getId());
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }
}
