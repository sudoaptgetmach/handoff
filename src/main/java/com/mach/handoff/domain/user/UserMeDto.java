package com.mach.handoff.domain.user;

import com.mach.handoff.domain.enums.roles.RoleName;

import java.util.Set;

public record UserMeDto(Long cid, String name, Set<RoleName> roles) {
}
