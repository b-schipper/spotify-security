package com.github.bschipper.spotifysecurity.integration;

import com.github.bschipper.spotifysecurity.controller.AuthenticationController;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {
        AuthenticationController.class,
        AuthenticationManager.class,
        PasswordEncoder.class})
@WebMvcTest
public class AuthenticationControllerIT {
    private final static String TEST_USER_ID = "user-id-123";
}
