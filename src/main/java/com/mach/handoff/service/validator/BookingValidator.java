package com.mach.handoff.service.validator;

import com.mach.handoff.domain.bookings.dto.CreateBookingDto;
import com.mach.handoff.domain.enums.events.EventStatus;
import com.mach.handoff.domain.events.Event;
import com.mach.handoff.domain.user.User;
import com.mach.handoff.domain.exception.ValidationException;
import com.mach.handoff.repository.BookingRepository;
import org.springframework.stereotype.Component;

@Component
public class BookingValidator {

    private final BookingRepository bookingRepository;

    public BookingValidator(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public void validate(CreateBookingDto dto, Event event, User user) {
        validateEventRules(event, dto);
        validateAvailability(dto, user);
    }

    private void validateEventRules(Event event, CreateBookingDto dto) {
        if (event.getStatus() != EventStatus.BOOKINGS_OPEN) {
            throw new ValidationException(
                    "Os bookings para o evento '" + event.getName() + "' não estão abertos."
            );
        }

        boolean isOutsideEvent = dto.startTime().isBefore(event.getStartTime())
                || dto.endTime().isAfter(event.getEndTime());

        if (isOutsideEvent) {
            throw new ValidationException(
                    "O horário da reserva deve estar compreendido dentro do horário do evento."
            );
        }
    }

    private void validateAvailability(CreateBookingDto dto, User user) {
        boolean isOccupied = bookingRepository.hasConfirmedOverlap(
                dto.position(),
                dto.startTime(),
                dto.endTime()
        );

        if (isOccupied) {
            throw new ValidationException(
                    "Já existe uma reserva confirmada para a posição " + dto.position() + " neste horário."
            );
        }

        boolean isDuplicate = bookingRepository.isDuplicate(
                user.getCid(),
                dto.position(),
                dto.startTime(),
                dto.endTime()
        );

        if (isDuplicate) {
            throw new ValidationException(
                    "Você já possui uma reserva conflitante solicitada."
            );
        }
    }
}