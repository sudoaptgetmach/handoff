package com.mach.handoff.repository;

import com.mach.handoff.domain.enums.events.EventStatus;
import com.mach.handoff.domain.events.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByVatsimId(Long vatsimId);

    Iterable<Event> findAllByVisibleAndStatusNot(boolean visible, EventStatus status);
}
