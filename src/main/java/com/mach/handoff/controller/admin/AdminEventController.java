package com.mach.handoff.controller.admin;

import com.mach.handoff.domain.events.Event;
import com.mach.handoff.service.EventService;
import com.mach.handoff.service.scheduler.VatsimEventSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Iterable<Event>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> findById(@PathVariable Long id) {
        Event event = service.findById(id);

        return ResponseEntity.ok(event);
    }

    @GetMapping("/sync")
    public ResponseEntity<Void> sync() {
        eventSyncService.syncEvents();

        return ResponseEntity.ok().build();
    }
}
