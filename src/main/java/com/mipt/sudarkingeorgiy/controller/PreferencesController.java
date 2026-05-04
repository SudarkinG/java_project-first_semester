package com.mipt.sudarkingeorgiy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/preferences")
@Tag(name = "Preferences", description = "User preferences via cookies")
public class PreferencesController {

    @Operation(summary = "Get current view preference from cookie")
    @GetMapping("/view")
    public ResponseEntity<Map<String, String>> getViewPreference(
            @CookieValue(value = "viewPreference", defaultValue = "compact") String mode) {
        return ResponseEntity.ok(Map.of("viewPreference", mode));
    }

    @Operation(summary = "Set view preference")
    @PostMapping("/view")
    public ResponseEntity<Map<String, String>> setViewPreference(@RequestParam String mode) {
        ResponseCookie cookie = ResponseCookie.from("viewPreference", mode)
                .path("/")
                .maxAge(30 * 24 * 60 * 60)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("viewPreference", mode));
    }
}
