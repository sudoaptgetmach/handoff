package com.mach.handoff.domain.bookings.dto;

import java.time.LocalDateTime;

public record CreateBookingDto(
        Long userCID,
        String position,
        Long eventId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
