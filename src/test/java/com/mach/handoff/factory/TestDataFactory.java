package com.mach.handoff.factory;

import com.mach.handoff.domain.bookings.Booking;
import com.mach.handoff.domain.bookings.dto.CreateBookingDto;
import com.mach.handoff.domain.enums.bookings.BookingStatus;
import com.mach.handoff.domain.enums.events.EventStatus;
import com.mach.handoff.domain.events.Event;
import com.mach.handoff.domain.user.User;

import java.time.LocalDateTime;

public class TestDataFactory {
    private static final LocalDateTime BASE_TIME = LocalDateTime.of(2024, 6, 15, 10, 0);

    public User createTestUser() {
        return createTestUser(1L, 1001L);
    }

    public User createTestUser(Long id, Long cid) {
        User user = new User();
        user.setId(id);
        user.setCid(cid);
        return user;
    }

    public CreateBookingDto createDto() {
        return createDto("Teste", 1L, BASE_TIME.plusHours(1), BASE_TIME.plusHours(3));
    }

    public CreateBookingDto createDto(String position, Long eventId, LocalDateTime start, LocalDateTime end) {
        return new CreateBookingDto(position, eventId, start, end);
    }

    public Event createOpenEvent() {
        return Event.builder()
                .id(1L)
                .status(EventStatus.BOOKINGS_OPEN)
                .startTime(BASE_TIME.plusHours(1))
                .endTime(BASE_TIME.plusHours(3))
                .visible(true)
                .build();
    }

    public Event createEvent(EventStatus status) {
        return Event.builder()
                .id(1L)
                .status(status)
                .startTime(BASE_TIME.plusHours(1))
                .endTime(BASE_TIME.plusHours(3))
                .visible(true)
                .build();
    }

    public Booking createBooking(User user, BookingStatus status) {
        return Booking.builder()
                .id(1L)
                .user(user)
                .status(status)
                .startTime(BASE_TIME.plusHours(1))
                .endTime(BASE_TIME.plusHours(3))
                .build();
    }
}