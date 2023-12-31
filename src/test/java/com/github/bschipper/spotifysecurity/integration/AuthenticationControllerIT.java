package com.github.bschipper.spotifysecurity.integration;

import com.github.bschipper.spotifysecurity.features.authentication.request.SigninRequest;
import com.github.bschipper.spotifysecurity.features.authentication.request.SignupRequest;
import com.github.bschipper.spotifysecurity.features.authentication.ERole;
import com.github.bschipper.spotifysecurity.features.authentication.Role;
import com.github.bschipper.spotifysecurity.features.authentication.RoleRepository;
import com.github.bschipper.spotifysecurity.features.user.UserRepository;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthenticationControllerIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationControllerIT.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    private Gson gson = new Gson();

    private String jwt;
    private String refreshtoken;

    @BeforeAll
    public void setup() throws Exception {
        Role admin = new Role(ERole.ROLE_ADMIN);
        Role user = new Role(ERole.ROLE_USER);
        Role artist = new Role(ERole.ROLE_ARTIST);
        roleRepository.saveAll(List.of(admin, user, artist));
    }

    @Test
    @Order(1)
    public void userRegistrationWithCorrectInputFormat_And_WithDuplicateUsernameAndEmail() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("AuthIntegrationTest");
        signupRequest.setEmail("auth@integration-test.nl");
        signupRequest.setPassword("12345678");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .content(gson.toJson(signupRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User successfully registered"));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .content(gson.toJson(signupRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    public void userAuthenticationWithExistingCredentials_And_WithNonExistentCredentials() throws Exception {
        SigninRequest signinReqExistCred = new SigninRequest();
        signinReqExistCred.setUsername("AuthIntegrationTest");
        signinReqExistCred.setPassword("12345678");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
                        .content(gson.toJson(signinReqExistCred))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.username").value("AuthIntegrationTest"))
                .andExpect(jsonPath("$.email").value("auth@integration-test.nl"))
                .andExpect(jsonPath("$.roles").isNotEmpty())
                .andReturn();

        SigninRequest signinReqNonExistCred = new SigninRequest();
        signinReqNonExistCred.setUsername("ThisUsernameDoesntExist");
        signinReqNonExistCred.setPassword("ThisPasswordIsInvalid");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
                        .content(gson.toJson(signinReqNonExistCred))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Bad credentials"));
    }
}
