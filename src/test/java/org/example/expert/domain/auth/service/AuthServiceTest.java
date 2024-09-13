package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    User testUser;
    SignupRequest testSignupRequest;

    @BeforeEach
    void setUp() {
        String email = "yuri123@naver.com";
        String password = "123456";
        String role = "USER";
        testSignupRequest = new SignupRequest(email, password, role);
        testUser = new User(email,password,UserRole.USER);
    }

    @Test
    void auth_정상등록 () {

        //given
        given(userRepository.existsByEmail(any())).willReturn(false);
        UserRole userRole = UserRole.of(testSignupRequest.getUserRole());
        User saveUser = new User(
                testSignupRequest.getEmail(),
                testSignupRequest.getPassword(),
                userRole
        );
        given(userRepository.save(any(User.class))).willReturn(saveUser);
        given(jwtUtil.createToken(saveUser.getId(), saveUser.getEmail(), userRole)).willReturn("serxgraeddtewdsgwetwsd");
        AuthService authService = new AuthService(userRepository, passwordEncoder, jwtUtil);

        //when
        SignupResponse result = authService.signup(testSignupRequest);

        //then
        assertEquals("serxgraeddtewdsgwetwsd", result.getBearerToken());
    }

    @Test
    void auth_이메일_중복() {
        //given
        given(userRepository.existsByEmail(any(String.class))).willReturn(true);
        AuthService authService = new AuthService(userRepository, passwordEncoder, jwtUtil);
        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> authService.signup(testSignupRequest));
        //then
        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
    }
}