package com.github.bschipper.spotifysecurity.repository;

import com.github.bschipper.spotifysecurity.models.ERole;
import com.github.bschipper.spotifysecurity.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
