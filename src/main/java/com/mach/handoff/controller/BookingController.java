package com.mach.handoff.controller;

import com.mach.handoff.domain.bookings.Booking;
import com.mach.handoff.domain.bookings.dto.BookingResponseDto;
import com.mach.handoff.domain.bookings.dto.CreateBookingDto;
import com.mach.handoff.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        return ResponseEntity.ok().body(new BookingResponseDto(newBooking));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        service.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
