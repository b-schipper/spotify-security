package com.github.bschipper.spotifysecurity.features.authentication;

import com.github.bschipper.spotifysecurity.features.authentication.request.TokenRefreshRequest;
import com.github.bschipper.spotifysecurity.features.authentication.response.TokenRefreshResponse;
import com.github.bschipper.spotifysecurity.features.authentication.request.SigninRequest;
import com.github.bschipper.spotifysecurity.features.authentication.request.SignupRequest;
import com.github.bschipper.spotifysecurity.features.authentication.response.JwtResponse;
import com.github.bschipper.spotifysecurity.features.MessageResponse;
import com.github.bschipper.spotifysecurity.security.CurrentUser;
import com.github.bschipper.spotifysecurity.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody SigninRequest request) {
        return ResponseEntity.ok(authenticationService.authenticateUser(
                request.getUsername(), request.getPassword()));
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.registerUser(
                request.getUsername(), request.getEmail(), request.getPassword(), ERole.ROLE_USER));
    }

    @PostMapping("/register/artist")
    public ResponseEntity<MessageResponse> registerArtist(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.registerUser(
                request.getUsername(), request.getEmail(), request.getPassword(), ERole.ROLE_ARTIST));
    }

    @PostMapping("/register/admin")
    public ResponseEntity<MessageResponse> registerAdmin(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.registerUser(
                request.getUsername(), request.getEmail(), request.getPassword(), ERole.ROLE_ADMIN));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(refreshTokenService.findByToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logoutUser(@CurrentUser UserPrincipal userPrincipal) {
        refreshTokenService.deleteByUserId(userPrincipal.getId());
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }
}
