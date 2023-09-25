package com.github.bschipper.spotifysecurity.controller;

import com.github.bschipper.spotifysecurity.exception.TokenRefreshException;
import com.github.bschipper.spotifysecurity.models.RefreshToken;
import com.github.bschipper.spotifysecurity.models.Role;
import com.github.bschipper.spotifysecurity.models.User;
import com.github.bschipper.spotifysecurity.payload.request.TokenRefreshRequest;
import com.github.bschipper.spotifysecurity.payload.request.SigninRequest;
import com.github.bschipper.spotifysecurity.payload.request.SignupRequest;
import com.github.bschipper.spotifysecurity.payload.response.JwtResponse;
import com.github.bschipper.spotifysecurity.payload.response.MessageResponse;
import com.github.bschipper.spotifysecurity.payload.response.TokenRefreshResponse;
import com.github.bschipper.spotifysecurity.repository.UserRepository;
import com.github.bschipper.spotifysecurity.security.RefreshTokenService;
import com.github.bschipper.spotifysecurity.security.TokenProvider;
import com.github.bschipper.spotifysecurity.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final TokenProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SigninRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String jwt = jwtProvider.generateToken(userPrincipal);

        //List<String> roles = userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userPrincipal.getId());

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                refreshToken.getToken(),
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getEmail()
                //roles
        ));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtProvider.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() ->
                        new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!")
                );
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userPrincipal.getId();
        refreshTokenService.deleteByUserId(userId);
        return ResponseEntity
                .ok(new MessageResponse("Log out successful!"));
    }
}
