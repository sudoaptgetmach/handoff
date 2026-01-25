package com.mach.handoff.controller;

import com.mach.handoff.domain.bookings.Booking;
import com.mach.handoff.domain.bookings.dto.BookingResponseDto;
import com.mach.handoff.domain.bookings.dto.CreateBookingDto;
import com.mach.handoff.domain.user.User;
import com.mach.handoff.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping("")
    public ResponseEntity<BookingResponseDto> create(@RequestBody CreateBookingDto dto,
                                                     @AuthenticationPrincipal User user) {
        Booking newBooking = service.create(dto, user);
        return ResponseEntity.ok().body(new BookingResponseDto(newBooking));
    }

    @GetMapping("/me")
    public ResponseEntity<List<BookingResponseDto>> myBookings(@AuthenticationPrincipal User user) {
        var bookings = service.getMyBookings(user);
        return ResponseEntity.ok(bookings);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable Long id,
                                        @AuthenticationPrincipal User user) {
        service.confirm(id, user);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id,
                                       @AuthenticationPrincipal User user) {
        service.cancel(id, user);
        return ResponseEntity.noContent().build();
    }
}
