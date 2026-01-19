package com.mach.handoff.controller;

import com.mach.handoff.domain.events.Event;
import com.mach.handoff.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping("")
    public ResponseEntity<Iterable<Event>> findVisibleEvents() {
        return ResponseEntity.ok(service.findVisibleEvents());
    }
}
