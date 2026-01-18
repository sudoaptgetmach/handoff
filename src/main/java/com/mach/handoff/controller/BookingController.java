package com.mach.handoff.controller;

import com.mach.handoff.domain.bookings.Booking;
import com.mach.handoff.domain.bookings.dto.BookingResponseDto;
import com.mach.handoff.domain.bookings.dto.CreateBookingDto;
import com.mach.handoff.exception.BookingNotFoundException;
import com.mach.handoff.repository.BookingRepository;
import com.mach.handoff.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService service;
    private final BookingRepository repository;

    public BookingController(BookingService service, BookingRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping("")
    public ResponseEntity<BookingResponseDto> create(@RequestBody CreateBookingDto dto) {
        Booking newBooking = service.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newBooking.getId())
                .toUri();

        return ResponseEntity.created(location).body(new BookingResponseDto(newBooking));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> get(@PathVariable Long id) {
        Optional<Booking> booking = repository.findById(id);

        if (booking.isEmpty()) {
            throw new BookingNotFoundException("Booking não encontrado.");
        }

        return ResponseEntity.ok().body(new BookingResponseDto(booking.get()));
    }
}
