package com.mipt.sudarkingeorgiy.api;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class SecuredInfoController {

    @GetMapping("/profile")
    public Map<String, Object> profile(Authentication authentication) {
        return Map.of(
                "username", authentication.getName(),
                "message", "Profile is available for USER role"
        );
    }

    @GetMapping("/docs")
    public Map<String, Object> docs(Authentication authentication) {
        return Map.of(
                "username", authentication.getName(),
                "message", "Docs are available for READ_PRIVILEGE"
        );
    }
}
