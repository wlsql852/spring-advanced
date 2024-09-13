package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Aspect
@Slf4j
public class AdminLogAop {

    @Pointcut("execution(* org.example.expert.domain.comment.controller.CommentAdminController.deleteComment(..))")
    private void commentDelete() {}

    @Pointcut("execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
    private void changeUserRole() {}

    @Around("commentDelete()||changeUserRole()")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        try {
            //핵심기능 수행
            return joinPoint.proceed();
        }finally {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            // 로그인 회원 정보
            AuthUser userDetails = (AuthUser) auth.getPrincipal();

            log.info("user's id : "+userDetails.getId()+", api request time : " + System.currentTimeMillis()+", api request url : "+request.getRequestURI());
        }

    }
}
