package com.mach.handoff.service;

import com.mach.handoff.domain.events.Event;
import com.mach.handoff.exception.EventNotFoundException;
import com.mach.handoff.repository.EventRepository;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public Event findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Evento não encontrado com id: " + id));
    }

    public Iterable<Event> findAll() {
        return repository.findAll();
    }
}
