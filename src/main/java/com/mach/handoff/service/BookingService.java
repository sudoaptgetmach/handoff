package com.mach.handoff.service;

import com.mach.handoff.domain.bookings.Booking;
import com.mach.handoff.domain.bookings.dto.CreateBookingDto;
import com.mach.handoff.domain.enums.bookings.BookingStatus;
import com.mach.handoff.domain.events.Event;
import com.mach.handoff.domain.user.User;
import com.mach.handoff.exception.EventNotFoundException;
import com.mach.handoff.exception.UserNotFoundException;
import com.mach.handoff.repository.BookingRepository;
import com.mach.handoff.repository.EventRepository;
import com.mach.handoff.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    public BookingService(UserRepository userRepository, BookingRepository bookingRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
    }

    public Booking create(CreateBookingDto dto) {
        Optional<User> user = userRepository.findByCid(dto.userId());
        Optional<Event> event = eventRepository.findById(dto.eventId());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Usuário não encontrado.");
        }
        if (event.isEmpty()) {
            throw new EventNotFoundException("Evento não encontrado.");
        }

        Booking booking = Booking.builder()
                .user(user.get())
                .event(event.get())
                .position(dto.position())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .status(BookingStatus.SOLICITADA)
                .build();

        bookingRepository.save(booking);

        return booking;
    }

}
