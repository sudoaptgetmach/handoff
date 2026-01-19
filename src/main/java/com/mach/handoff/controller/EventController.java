package com.mach.handoff.controller;

import com.mach.handoff.domain.bookings.Booking;
import com.mach.handoff.domain.bookings.dto.BookingResponseDto;
import com.mach.handoff.domain.enums.bookings.BookingStatus;
import com.mach.handoff.domain.events.Event;
import com.mach.handoff.service.BookingService;
import com.mach.handoff.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService service;
    private final BookingService bookingService;

    public EventController(EventService service, BookingService bookingService) {
        this.service = service;
        this.bookingService = bookingService;
    }

    @GetMapping("")
    public ResponseEntity<Iterable<Event>> findVisibleEvents() {
        return ResponseEntity.ok(service.getAllVisible());
    }

    @GetMapping("/{id}/roster")
    public ResponseEntity<List<BookingResponseDto>> roster(@PathVariable Long id) {
        List<Booking> bookings = bookingService.get(id, BookingStatus.CONFIRMADO);

        List<BookingResponseDto> dtos = bookings.stream()
                .map(BookingResponseDto::new)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
