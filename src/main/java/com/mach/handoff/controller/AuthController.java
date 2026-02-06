package com.mach.handoff.controller;

import com.mach.handoff.domain.user.User;
import com.mach.handoff.service.AuthService;
import com.mach.handoff.service.auth.TokenService;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/login")
public class AuthController {

    private static final String AUTH_COOKIE_NAME = "auth_token";
    private final AuthService authService;
    private final TokenService tokenService;

    public AuthController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @GetMapping("/success")
    public ResponseEntity<Map<String, String>> handleLoginSuccess(@AuthenticationPrincipal OAuth2User principal) {
        User user = authService.syncUser(principal);

        String token = tokenService.generateToken(user);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "cid", user.getCid().toString()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from(AUTH_COOKIE_NAME, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.noContent()
                .header("Set-Cookie", cookie.toString())
                .build();
    }
}
