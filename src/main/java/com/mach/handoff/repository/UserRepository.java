package com.mach.handoff.repository;

import com.mach.handoff.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCid(Long cid);
}
