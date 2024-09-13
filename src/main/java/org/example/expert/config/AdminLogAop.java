package org.example.expert.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class AdminLogAop {

    private final HttpServletRequest httpRequest;
    private final JwtUtil jwtUtil;

    public AdminLogAop(HttpServletRequest httpRequest, JwtUtil jwtUtil) {
        this.httpRequest = httpRequest;
        this.jwtUtil = jwtUtil;
    }


    @Pointcut("execution(* org.example.expert.domain.comment.controller.CommentAdminController.deleteComment(..))")
    private void commentDelete() {}

    @Pointcut("execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
    private void changeUserRole() {}

    @Around("commentDelete()||changeUserRole()")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            //핵심기능 수행
            return joinPoint.proceed();
        }finally {
            // 로그인 회원 정보
            String bearerJwt = httpRequest.getHeader("Authorization");
            String jwt = jwtUtil.substringToken(bearerJwt);
            Claims claims = jwtUtil.extractClaims(jwt);
            Long userId = Long.parseLong(claims.getSubject());

            log.info("user's id : "+userId+", api request time : " + System.currentTimeMillis()+", api request url : "+httpRequest.getRequestURI());
        }
    }
}
