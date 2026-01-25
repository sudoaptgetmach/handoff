package com.mach.handoff.service.validator;

import com.mach.handoff.domain.events.Event;
import com.mach.handoff.domain.events.dto.CreateEventDto;
import com.mach.handoff.domain.exception.ValidationException;
import com.mach.handoff.repository.EventRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class EventValidator {
    private final EventRepository repository;

    public EventValidator(EventRepository repository) {
        this.repository = repository;
    }

    public void validate(CreateEventDto dto) {
        checkDuplicate(dto.name(), dto.startTime(), dto.endTime());
    }

    public void checkDuplicate(String name, LocalDateTime startTime, LocalDateTime endTime) {
        Optional<Event> event = repository.findByName(name);

        if (event.isEmpty()) return;

        boolean hasDuplicate = event.get().getStartTime().isEqual(startTime) || event.get().getEndTime().isEqual(endTime);

        if (hasDuplicate) {
            throw new ValidationException("Já existe um evento conflitante criado.");
        }
    }
}
