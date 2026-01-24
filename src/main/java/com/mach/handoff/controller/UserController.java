package com.mach.handoff.controller;

import com.mach.handoff.domain.enums.roles.RoleName;
import com.mach.handoff.domain.roles.Role;
import com.mach.handoff.domain.user.User;
import com.mach.handoff.domain.user.UserMeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private static UserMeDto toMeDto(User user) {
        Set<RoleName> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toUnmodifiableSet());

        return new UserMeDto(
                user.getCid(),
                user.getName(),
                roleNames
        );
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeDto> getMe(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(toMeDto(user));
    }
}