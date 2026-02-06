package com.mach.handoff.service;

import com.mach.handoff.domain.bookings.Booking;
import com.mach.handoff.domain.bookings.dto.BookingResponseDto;
import com.mach.handoff.domain.bookings.dto.CreateBookingDto;
import com.mach.handoff.domain.enums.bookings.BookingStatus;
import com.mach.handoff.domain.enums.roles.RoleName;
import com.mach.handoff.domain.exception.ForbiddenException;
import com.mach.handoff.domain.exception.NotFoundException;
import com.mach.handoff.domain.user.User;
import com.mach.handoff.repository.BookingRepository;
import com.mach.handoff.repository.EventRepository;
import com.mach.handoff.service.validator.BookingValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    private final BookingValidator validator;

    public BookingService(BookingRepository bookingRepository, EventRepository eventRepository, BookingValidator validator) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.validator = validator;
    }

    public Booking create(CreateBookingDto dto, User user) {
        var event = eventRepository.findById(dto.eventId())
                .orElseThrow(() -> new NotFoundException("Event not found."));

        validator.validate(dto, event, user);

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

    public List<BookingResponseDto> getMyBookings(User user) {
        return bookingRepository.findByUserCidOrderByStartTimeDesc(user.getCid())
                .stream()
                .map(BookingResponseDto::new)
                .toList();
    }

    public void confirm(Long id, User requester) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);

        if (bookingOpt.isEmpty()) {
            throw new NotFoundException("Booking not found.");
        }

        Booking booking = bookingOpt.get();

        if (!requester.equals(booking.getUser())) {
            throw new ForbiddenException("You can't confirm this booking.");
        }

        booking.confirm();

        bookingRepository.save(booking);
    }

    public void cancel(Long bookingId, User requester) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found."));

        boolean isOwner = booking.getUser().getCid().equals(requester.getCid());
        boolean isAdmin = requester.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.ADMIN || r.getName() == RoleName.STAFF);

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("You don't have permission to cancel this booking.");
        }

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

    public Booking get(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found."));
    }

    public void approve(Long id, User user) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found."));

        booking.approve(user);

        List<Booking> bookings = bookingRepository.findAllOtherBookingsForEvent(user.getCid(), booking.getEvent().getId(), id);
        bookings.forEach(Booking::cancel);

        bookingRepository.save(booking);
    }

    public void reject(Long id, User user) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found."));

        booking.reject(user);

        bookingRepository.save(booking);
    }
}
