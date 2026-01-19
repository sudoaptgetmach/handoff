package com.mach.handoff.controller.admin;

import com.mach.handoff.domain.bookings.Booking;
import com.mach.handoff.domain.bookings.dto.BookingResponseDto;
import com.mach.handoff.domain.user.User;
import com.mach.handoff.exception.NotFoundException;
import com.mach.handoff.repository.UserRepository;
import com.mach.handoff.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    private final UserRepository userRepository;
    private final BookingService service;

    public AdminBookingController(UserRepository userRepository, BookingService service) {
        this.userRepository = userRepository;
        this.service = service;
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<BookingResponseDto>> listByEvent(@PathVariable Long eventId) {
        List<Booking> bookings = service.getAllByEventID(eventId);

        List<BookingResponseDto> dtos = bookings.stream()
                .map(BookingResponseDto::new)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long id, @RequestBody Long userCID) {
        User user = userRepository.findByCid(userCID)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        service.approveBooking(id, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long id, @RequestBody Long userCID) {
        User user = userRepository.findByCid(userCID)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        service.rejectBooking(id, user);
        return ResponseEntity.noContent().build();
    }
}