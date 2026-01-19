package com.mach.handoff.service;

import com.mach.handoff.domain.enums.events.EventStatus;
import com.mach.handoff.domain.events.Event;
import com.mach.handoff.exception.NotFoundException;
import com.mach.handoff.repository.EventRepository;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public Iterable<Event> findVisibleEvents() {
        return repository.findAllByVisibleAndStatusNot(true, EventStatus.DRAFT);
    }

    public Event findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado com id: " + id));
    }

    public Iterable<Event> findAll() {
        return repository.findAll();
    }
}
