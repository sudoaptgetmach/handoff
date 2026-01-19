package com.mach.handoff.service;

import com.mach.handoff.domain.bookings.Booking;
import com.mach.handoff.domain.bookings.dto.CreateBookingDto;
import com.mach.handoff.domain.enums.bookings.BookingStatus;
import com.mach.handoff.domain.user.User;
import com.mach.handoff.exception.NotFoundException;
import com.mach.handoff.repository.BookingRepository;
import com.mach.handoff.repository.EventRepository;
import com.mach.handoff.repository.UserRepository;
import com.mach.handoff.service.validator.BookingValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    private final BookingValidator validator;

    public BookingService(UserRepository userRepository, BookingRepository bookingRepository, EventRepository eventRepository, BookingValidator validator) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.validator = validator;
    }

    public Booking create(CreateBookingDto dto) {
        var user = userRepository.findByCid(dto.userCID())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        var event = eventRepository.findById(dto.eventId())
                .orElseThrow(() -> new NotFoundException("Evento não encontrado."));

        validator.validate(dto, event);

        Booking booking = Booking.builder()
                .user(user)
                .event(event)
                .position(dto.position())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .status(BookingStatus.SOLICITADO)
                .build();

        bookingRepository.save(booking);

        return booking;
    }

    public Booking get(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking não encontrado com id: " + id));
    }

    public void cancel(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking não encontrado."));

        booking.cancel();

        bookingRepository.save(booking);
    }

    // ADMIN

    public List<Booking> get(Long id, BookingStatus status) {
        if (id != null && status != null) {
            return bookingRepository.findAllByEvent_IdAndStatus(id, status);
        }

        if (status != null) {
            return bookingRepository.findAllByStatus(status);
        }

        if (id != null) {
            return bookingRepository.findAllByEvent_Id(id);
        }

        return bookingRepository.findAll();
    }

    public void approve(Long id, User user) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking não encontrado."));

        booking.approve(user);

        List<Booking> bookings = bookingRepository.findAllOtherBookingsForEvent(user.getCid(), booking.getEvent().getId(), id);
        bookings.forEach(Booking::cancel);

        bookingRepository.save(booking);
    }

    public void reject(Long id, User user) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking não encontrado."));

        booking.reject(user);

        bookingRepository.save(booking);
    }
}
