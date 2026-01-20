package com.mach.handoff.domain.events.dto;

public record UpdateEventDto(
        String name,
        String description,
        String bannerUrl
) {
}