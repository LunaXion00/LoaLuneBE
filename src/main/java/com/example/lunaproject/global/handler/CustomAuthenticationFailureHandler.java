package com.example.lunaproject.global.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        // 로그인 실패 시 로그인 실패 횟수를 증가하도록 trigger
        if (exception instanceof BadCredentialsException) {
            increaseLoginFailCount(request.getParameter("username"));
        }

        //해당 메세지를 상황에 따라 화면으로 회신 한다.
        request.setAttribute("errorMsg", exception.getMessage());
        request.getRequestDispatcher("/loginPage?error=true").forward(request, response);
    }

    private void increaseLoginFailCount(String username) {
        //로그인 실패 시 로그인 실패 횟수를 증가시키는 함수
    }
}