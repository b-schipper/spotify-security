package com.github.bschipper.spotifysecurity.unit;

import com.github.bschipper.spotifysecurity.features.authentication.ERole;
import com.github.bschipper.spotifysecurity.features.authentication.Role;
import com.github.bschipper.spotifysecurity.features.authentication.RoleRepository;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AuthenticationControllerTests {
    @Autowired
    private RoleRepository roleRepository;

    @BeforeAll
    public void setup() {
        Role admin = new Role(ERole.ROLE_ADMIN);
        Role user = new Role(ERole.ROLE_USER);
        Role artist = new Role(ERole.ROLE_ARTIST);
        roleRepository.saveAll(List.of(admin, user, artist));
    }
}
