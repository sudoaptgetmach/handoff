package com.mach.handoff.controller.admin;

import com.mach.handoff.domain.enums.events.EventStatus;
import com.mach.handoff.domain.events.Event;
import com.mach.handoff.domain.events.dto.CreateEventDto;
import com.mach.handoff.service.EventService;
import com.mach.handoff.service.scheduler.VatsimEventSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService service;
    private final VatsimEventSyncService eventSyncService;

    public AdminEventController(EventService service, VatsimEventSyncService eventSyncService) {
        this.service = service;
        this.eventSyncService = eventSyncService;
    }

    @PostMapping("")
    public ResponseEntity<Event> create(@RequestBody CreateEventDto event) {

        Event newEvent = service.create(event);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("?eventId={id}")
                .buildAndExpand(newEvent.getId())
                .toUri();

        return ResponseEntity.created(location).body(newEvent);
    }

    @GetMapping("")
    public ResponseEntity<List<Event>> get(@RequestParam(required = false) Long eventId,
                                           @RequestParam(required = false) EventStatus status
    ) {
        List<Event> events = service.get(eventId, status);

        return ResponseEntity.ok(events);
    }

    @GetMapping("/sync")
    public ResponseEntity<Void> sync() {
        eventSyncService.syncEvents();

        return ResponseEntity.ok().build();
    }
}
