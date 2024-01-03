package com.github.bschipper.spotifysecurity.features.user;

import com.github.bschipper.spotifysecurity.features.authentication.Role;
import com.github.bschipper.spotifysecurity.features.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    int countAllByRolesContaining(Role role);
}
