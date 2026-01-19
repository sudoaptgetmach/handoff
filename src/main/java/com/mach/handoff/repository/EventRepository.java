package com.mach.handoff.repository;

import com.mach.handoff.domain.enums.events.EventStatus;
import com.mach.handoff.domain.events.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByVatsimId(Long vatsimId);

    Optional<Event> findByName(String name);

    List<Event> findAllById(Long id);

    List<Event> findAllByStatus(EventStatus status);

    List<Event> findAllByIdAndStatus(Long id, EventStatus status);

    List<Event> findAllByVisibleTrue();
}
