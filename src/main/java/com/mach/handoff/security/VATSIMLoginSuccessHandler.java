package com.mach.handoff.security;

import com.mach.handoff.domain.user.User;
import com.mach.handoff.service.AuthService;
import com.mach.handoff.service.auth.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class VATSIMLoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final String AUTH_COOKIE_NAME = "auth_token";
    private final AuthService authService;
    private final TokenService tokenService;

    public VATSIMLoginSuccessHandler(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        User user = authService.syncUser(Objects.requireNonNull(oAuth2User));

        String token = tokenService.generateToken(user);

        ResponseCookie cookie = ResponseCookie.from(AUTH_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(request.isSecure())
                .path("/")
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        response.sendRedirect("/app/");
    }
}
