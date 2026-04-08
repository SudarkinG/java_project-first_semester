package com.mipt.sudarkingeorgiy.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiVersionInterceptor implements HandlerInterceptor {

    @Value("${app.api-version}")
    private String apiVersion;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        response.setHeader("X-API-Version", apiVersion);
        return true;
    }
}
