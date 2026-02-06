package com.mach.handoff.service;

import com.mach.handoff.domain.enums.roles.RoleName;
import com.mach.handoff.domain.roles.Role;
import com.mach.handoff.domain.user.User;
import com.mach.handoff.repository.RoleRepository;
import com.mach.handoff.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public User syncUser(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        Map<String, Object> data = (Map<String, Object>) attributes.get("data");

        if (data == null) {
            throw new RuntimeException("Error: 'data' object was never returned by VATSIM.");
        }

        Object cidObj = data.get("cid");
        Long cid = Long.valueOf(String.valueOf(cidObj));

        Map<String, Object> personal = (Map<String, Object>) data.get("personal");
        String fullName = (String) personal.get("name_full");

        return userRepository.findByCid(cid)
                .map(existingUser -> {
                    existingUser.setName(fullName);
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setCid(cid);
                    newUser.setName(fullName);

                    Role defaultRole = roleRepository.findByName(RoleName.USER)
                            .orElseThrow(() -> new RuntimeException("USER role not found."));

                    newUser.setRoles(Set.of(defaultRole));

                    return userRepository.save(newUser);
                });
    }
}