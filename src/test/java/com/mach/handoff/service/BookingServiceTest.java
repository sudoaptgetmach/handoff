package com.mach.handoff.service;

import com.mach.handoff.domain.bookings.Booking;
import com.mach.handoff.domain.bookings.dto.CreateBookingDto;
import com.mach.handoff.domain.enums.bookings.BookingStatus;
import com.mach.handoff.domain.enums.events.EventStatus;
import com.mach.handoff.domain.events.Event;
import com.mach.handoff.domain.exception.DomainException;
import com.mach.handoff.domain.exception.ForbiddenException;
import com.mach.handoff.domain.exception.NotFoundException;
import com.mach.handoff.domain.exception.ValidationException;
import com.mach.handoff.domain.user.User;
import com.mach.handoff.factory.TestDataFactory;
import com.mach.handoff.repository.BookingRepository;
import com.mach.handoff.repository.EventRepository;
import com.mach.handoff.service.validator.BookingValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private BookingValidator bookingValidator;
    @InjectMocks
    private BookingService bookingService;

    TestDataFactory dataFactory = new TestDataFactory();

    @Test
    void create_Success() {
        User requester = dataFactory.createTestUser();
        Event event = dataFactory.createOpenEvent();
        CreateBookingDto dto = dataFactory.createDto();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Booking booking = bookingService.create(dto, requester);

        verify(bookingRepository).save(booking);
        verify(bookingValidator).validate(dto, event, requester);

        assertEquals(booking.getUser(), requester);
        assertEquals(booking.getEvent(), event);
        assertEquals(booking.getPosition(), dto.position());
        assertEquals(booking.getStartTime(), dto.startTime());
        assertEquals(booking.getEndTime(), dto.endTime());
        assertEquals(BookingStatus.SOLICITADO, booking.getStatus());
    }

    @Test
    void create_whenEventNotFound_throwsNotFound() {
        User requester = dataFactory.createTestUser();
        CreateBookingDto dto = dataFactory.createDto();

        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(dto, requester));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_whenEventRulesAreNotMet_throwsValidationError() {
        User requester = dataFactory.createTestUser();
        Event event = dataFactory.createEvent(EventStatus.BOOKINGS_CLOSED);
        CreateBookingDto dto = dataFactory.createDto();

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        doThrow(new ValidationException("Regras do evento não atendidas."))
                .when(bookingValidator)
                .validate(dto, event, requester);

        assertThrows(ValidationException.class, () -> bookingService.create(dto, requester));
        verify(bookingValidator).validate(dto, event, requester);
        verify(bookingRepository, never()).save(any());
    }

    // Confirm method

    @Test
    void confirm_whenRequesterIsOwner_confirmsAndSaves() {
        User requester = dataFactory.createTestUser();
        Booking booking = dataFactory.createBooking(requester, BookingStatus.ATRIBUIDO);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        bookingService.confirm(booking.getId(), requester);

        assertEquals(BookingStatus.CONFIRMADO, booking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void confirm_whenBookingNotFound_throwsNotFound() {
        User requester = dataFactory.createTestUser();

        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.confirm(999L, requester));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void confirm_whenRequesterIsNotOwner_throwsForbidden() {
        User owner = dataFactory.createTestUser();
        User requester = dataFactory.createTestUser(2L, 2002L);
        Booking booking = dataFactory.createBooking(owner, BookingStatus.ATRIBUIDO);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.confirm(1L, requester));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void confirm_whenStatusCanceladoAndTransitionIsInvalid_throwsDomain() {
        User requester = dataFactory.createTestUser();
        Booking booking = dataFactory.createBooking(requester, BookingStatus.CANCELADO);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(DomainException.class, () -> bookingService.confirm(1L, requester));
        verify(bookingRepository, never()).save(any());
    }

    // Cancel
    @Test
    void cancel_Success() {
        User requester = dataFactory.createTestUser();

        Booking booking = dataFactory.createBooking(requester, BookingStatus.SOLICITADO);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        bookingService.cancel(booking.getId(), requester);

        assertEquals(BookingStatus.CANCELADO, booking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void cancel_whenRequesterIsNotOwner_throwsForbidden() {
        User owner = dataFactory.createTestUser();

        User requester = dataFactory.createTestUser(2L, 2002L);

        Booking booking = dataFactory.createBooking(owner, BookingStatus.SOLICITADO);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.cancel(1L, requester));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void cancel_whenRequesterIsAdminButNotOwner_success() {
        User owner = dataFactory.createTestUser(1L, 1001L);
        User admin = dataFactory.createAdminUser(2L, 2002L);

        Booking booking = dataFactory.createBooking(owner, BookingStatus.SOLICITADO);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.cancel(1L, admin);

        assertEquals(BookingStatus.CANCELADO, booking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void cancel_whenStatusIsAlreadyCancelled_throwsDomain() {
        User requester = dataFactory.createTestUser();

        Booking booking = dataFactory.createBooking(requester, BookingStatus.CANCELADO);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(DomainException.class, () -> bookingService.cancel(1L, requester));
        verify(bookingRepository, never()).save(any());
    }
}
