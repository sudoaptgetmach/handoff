package com.mach.handoff.controller.admin;

import com.mach.handoff.domain.enums.events.EventStatus;
import com.mach.handoff.domain.events.Event;
import com.mach.handoff.service.EventService;
import com.mach.handoff.service.scheduler.VatsimEventSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
