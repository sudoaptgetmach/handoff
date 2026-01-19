package com.mach.handoff.service;

import com.mach.handoff.domain.enums.events.EventStatus;
import com.mach.handoff.domain.events.Event;
import com.mach.handoff.domain.events.dto.CreateEventDto;
import com.mach.handoff.repository.EventRepository;
import com.mach.handoff.service.validator.EventValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final EventRepository repository;

    private final EventValidator validator;

    public EventService(EventRepository repository, EventValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public List<Event> getAllVisible() {
        return repository.findAllByVisibleTrue();
    }

    // ADMIN

    public Event create(CreateEventDto dto) {
        validator.validate(dto);

        Event event = Event.builder()
                .name(dto.name())
                .description(dto.description())
                .bannerUrl(dto.bannerUrl())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .airports(dto.airports())
                .status(dto.status())
                .visible(dto.visible())
                .build();

        return repository.save(event);
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
}
