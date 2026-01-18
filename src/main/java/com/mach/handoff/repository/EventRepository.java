package com.mach.handoff.repository;

import com.mach.handoff.domain.events.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
