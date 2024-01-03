package com.github.bschipper.spotifysecurity.features.authentication;

import com.github.bschipper.spotifysecurity.features.MessageResponse;
import com.github.bschipper.spotifysecurity.features.user.User;
import com.github.bschipper.spotifysecurity.features.authentication.response.JwtResponse;
import com.github.bschipper.spotifysecurity.features.user.UserRepository;
import com.github.bschipper.spotifysecurity.security.UserPrincipal;
import com.github.bschipper.spotifysecurity.security.services.JwtUtil;
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
        if (refreshTokenService.existsByUserId(userPrincipal.getId())) {
            refreshTokenService.deleteByUserId(userPrincipal.getId());
        }

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
            throw new IllegalArgumentException("Username is already in use");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email address is already in use");
        }

        if (!roleRepository.existsById(1L)) {
            Role adminRole = new Role(ERole.ROLE_ADMIN);
            Role userRole = new Role(ERole.ROLE_USER);
            Role artistRole = new Role(ERole.ROLE_ARTIST);
            roleRepository.saveAll(List.of(adminRole, userRole, artistRole));
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
        } else if (role == ERole.ROLE_ADMIN) {
            roles.add(roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: ROLE_USER is not found")));
            roles.add(roleRepository.findByName(ERole.ROLE_ARTIST)
                    .orElseThrow(() -> new RuntimeException("Error: ROLE_ARTIST is not found")));
            roles.add(roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: ROLE_ADMIN is not found")));
            user.setRoles(roles);
        }
        userRepository.save(user);

        return new MessageResponse("User successfully registered");
    }
}
