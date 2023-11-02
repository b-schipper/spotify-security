package com.github.bschipper.spotifysecurity.security.services;

import com.github.bschipper.spotifysecurity.controller.payload.response.MessageResponse;
import com.github.bschipper.spotifysecurity.models.ERole;
import com.github.bschipper.spotifysecurity.models.Role;
import com.github.bschipper.spotifysecurity.exception.BadRequestException;
import com.github.bschipper.spotifysecurity.models.User;
import com.github.bschipper.spotifysecurity.controller.payload.response.JwtResponse;
import com.github.bschipper.spotifysecurity.repository.RoleRepository;
import com.github.bschipper.spotifysecurity.repository.UserRepository;
import com.github.bschipper.spotifysecurity.security.UserPrincipal;
import com.github.bschipper.spotifysecurity.models.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public JwtResponse authenticateUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String jwt = jwtUtil.generateToken(userPrincipal);

        List<String> roles = userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userPrincipal.getId());

        return new JwtResponse(
                jwt,
                refreshToken.getToken(),
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getEmail(),
                roles
        );
    }

    public MessageResponse registerUser(String username, String email, String password, ERole role) {
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username is already in use");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email address is already in use");
        }

        if (!roleRepository.existsById(1L)) {
            Role admin = new Role(ERole.ROLE_ADMIN);
            Role userr = new Role(ERole.ROLE_USER);
            Role artist = new Role(ERole.ROLE_ARTIST);
            roleRepository.saveAll(List.of(admin, userr, artist));
        }

        // Create new user's account
        User user = new User(username, email, encoder.encode(password));
        Set<Role> roles = new HashSet<>();
        if (role == ERole.ROLE_USER) {
            roles.add(roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: ROLE_USER is not found")));
            user.setRoles(roles);
        } else if (role == ERole.ROLE_ARTIST) {
            roles.add(roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: ROLE_USER is not found")));
            roles.add(roleRepository.findByName(ERole.ROLE_ARTIST)
                    .orElseThrow(() -> new RuntimeException("Error: ROLE_ARTIST is not found")));
            user.setRoles(roles);
        }
        userRepository.save(user);

        return new MessageResponse("User successfully registered");
    }

    public void logoutUser(Long userId) {
        refreshTokenService.deleteByUserId(userId);
    }
}
