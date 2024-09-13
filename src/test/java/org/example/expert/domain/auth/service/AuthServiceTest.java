package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    AuthService authService;

    @Test
    void auth_정상등록 () {

        //given

        String email = "yuri123@naver.com";
        String password = "123456";
        String role = "USER";
        SignupRequest signupRequest = new SignupRequest(email, password, role);
        given(userRepository.existsByEmail(email)).willReturn(false);
        given(passwordEncoder.encode(password)).willReturn(password);
        UserRole userRole = UserRole.of(signupRequest.getUserRole());
        User newUser = new User(
                signupRequest.getEmail(),
                password,
                userRole
        );
        User saveUser = new User(
                signupRequest.getEmail(),
                password,
                userRole
        );
        given(userRepository.save(any(User.class))).willReturn(saveUser);
        given(jwtUtil.createToken(saveUser.getId(), saveUser.getEmail(), userRole)).willReturn("serxgraeddtewdsgwetwsd");
        AuthService authService = new AuthService(userRepository, passwordEncoder, jwtUtil);

        //when
        SignupResponse result = authService.signup(signupRequest);

        //then
        assertEquals("serxgraeddtewdsgwetwsd", result.getBearerToken());
    }
}