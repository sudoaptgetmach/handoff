package com.mach.handoff.domain.bookings.dto;

import java.time.LocalDateTime;

public record CreateBookingDto(
        String position,
        Long eventId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
