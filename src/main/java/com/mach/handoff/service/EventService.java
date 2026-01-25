package com.mach.handoff.service;

import com.mach.handoff.domain.bookings.Booking;
import com.mach.handoff.domain.enums.events.EventStatus;
import com.mach.handoff.domain.events.Event;
import com.mach.handoff.domain.events.dto.CreateEventDto;
import com.mach.handoff.domain.events.dto.UpdateEventDto;
import com.mach.handoff.domain.exception.NotFoundException;
import com.mach.handoff.repository.BookingRepository;
import com.mach.handoff.repository.EventRepository;
import com.mach.handoff.service.validator.EventValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final EventRepository repository;
    private final BookingRepository bookingRepository;

    private final EventValidator validator;

    public EventService(EventRepository repository, BookingRepository bookingRepository, EventValidator validator) {
        this.repository = repository;
        this.bookingRepository = bookingRepository;
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

    @Transactional
    public Event update(Long id, UpdateEventDto dto) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        if (dto.name() != null) event.setName(dto.name());
        if (dto.description() != null) event.setDescription(dto.description());
        if (dto.bannerUrl() != null) event.setBannerUrl(dto.bannerUrl());

        repository.save(event);

        return event;
    }

    @Transactional
    public void cancel(Long id) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        event.setStatus(EventStatus.CANCELLED);
        event.setVisible(false);

        List<Booking> activeBookings = bookingRepository.findAllActiveByEvent(event);
        activeBookings.forEach(Booking::cancel);
        bookingRepository.saveAll(activeBookings);


        repository.save(event);
    }
}
