package com.mach.handoff.domain.bookings.dto;

import com.mach.handoff.domain.bookings.Booking;

import java.time.LocalDateTime;

public record BookingResponseDto(
        Long id,
        Long userId,
        Long eventId,
        String position,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status
) {
    public BookingResponseDto(Booking booking) {
        this(
                booking.getId(),
                booking.getUser().getCid(),
                booking.getEvent().getId(),
                booking.getPosition(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus().toString()
        );
    }
}