package com.mach.handoff.domain.bookings.dto;

import com.mach.handoff.domain.bookings.Booking;

import java.time.LocalDateTime;

public record BookingResponseDto(
        Long id,
        String position,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long userId,
        String userName,
        Long eventId
) {
    public BookingResponseDto(Booking booking) {
        this(
                booking.getId(),
                booking.getPosition(),
                booking.getStatus().toString(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getUser().getId(),
                booking.getUser().getName(),
                booking.getEvent().getId()
        );
    }
}