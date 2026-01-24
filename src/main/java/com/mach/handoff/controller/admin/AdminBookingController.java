package com.mach.handoff.controller.admin;

import com.mach.handoff.domain.bookings.Booking;
import com.mach.handoff.domain.bookings.dto.BookingResponseDto;
import com.mach.handoff.domain.enums.bookings.BookingStatus;
import com.mach.handoff.domain.user.User;
import com.mach.handoff.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    private final BookingService service;

    public AdminBookingController(BookingService service) {
        this.service = service;
    }

    @GetMapping("")
    public ResponseEntity<List<BookingResponseDto>> get(@RequestParam(required = false) Long eventId,
                                                        @RequestParam(required = false) BookingStatus status
    ) {
        List<Booking> bookings = service.get(eventId, status);

        List<BookingResponseDto> dtos = bookings.stream()
                .map(BookingResponseDto::new)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long id,
                                        @AuthenticationPrincipal User user) {
        service.approve(id, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long id,
                                       @AuthenticationPrincipal User user) {
        service.reject(id, user);
        return ResponseEntity.noContent().build();
    }
}