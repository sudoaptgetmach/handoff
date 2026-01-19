package com.mach.handoff.service;

import com.mach.handoff.domain.enums.events.EventStatus;
import com.mach.handoff.domain.events.Event;
import com.mach.handoff.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public List<Event> get(Long id, EventStatus status) {
        if (id != null && status != null) {
            return repository.findAllByIdAndStatus(id, status);
        }

        if (status != null) {
            return repository.findAllByStatus(status);
        }

        if (id != null) {
            return repository.findAllById(id);
        }

        return repository.findAll();
    }

    public List<Event> getAllVisible() {
        return repository.findAllByVisibleTrue();
    }
}
