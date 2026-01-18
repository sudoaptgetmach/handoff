package com.mach.handoff.controller;

import com.mach.handoff.domain.bookings.Booking;
import com.mach.handoff.domain.bookings.dto.BookingResponseDto;
import com.mach.handoff.domain.bookings.dto.CreateBookingDto;
import com.mach.handoff.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
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
        Booking booking = service.get(id);
        return ResponseEntity.ok(new BookingResponseDto(booking));
    }
}
