package com.mach.handoff.service;

import com.mach.handoff.domain.events.Event;
import com.mach.handoff.domain.events.dto.CreateEventDto;
import com.mach.handoff.factory.TestDataFactory;
import com.mach.handoff.repository.BookingRepository;
import com.mach.handoff.repository.EventRepository;
import com.mach.handoff.service.validator.EventValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    TestDataFactory dataFactory = new TestDataFactory();
    @Mock
    private EventRepository repository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private EventValidator validator;
    @InjectMocks
    private EventService eventService;

    @Test
    void getAllVisibleEvents_whenVisibleEventsAvailable_returnsEvents() {
        Event event = dataFactory.createOpenEvent();
        event.setName("Teste");

        when(repository.findAllByVisibleTrue()).thenReturn(List.of(event));

        List<Event> result = eventService.getAllVisible();

        assertThat(result)
                .isNotEmpty()
                .extracting(Event::getName)
                .containsExactly("Teste");
    }

    @Test
    void getAllVisibleEvents_whenNoVisibleEventsAvailable_returnsEmptyList() {
        when(repository.findAllByVisibleTrue()).thenReturn(List.of());

        List<Event> events = eventService.getAllVisible();

        assertThat(events).isEmpty();
    }

    // ADMIN

    @Test
    void create_whenUserIsAdminAndEventIsValid_createsAndSavesEvent() {
        CreateEventDto createEventDto = dataFactory.createEventDto();

        eventService.create(createEventDto);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);

        verify(repository).save(eventCaptor.capture());
        verify(validator).validate(createEventDto);

        Event eventoSalvo = eventCaptor.getValue();

        assertEquals(createEventDto.name(), eventoSalvo.getName());
    }

    @Test
    void get() {
    }

    @Test
    void update() {
    }

    @Test
    void cancel() {
    }
}