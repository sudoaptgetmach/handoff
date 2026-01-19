package com.mach.handoff.domain.events.dto;

import com.mach.handoff.domain.enums.events.EventStatus;

import java.time.LocalDateTime;
import java.util.Set;

public record CreateEventDto(
        String name,
        String description,
        String bannerUrl,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Set<String> airports,
        EventStatus status,
        Boolean visible
) {
}
