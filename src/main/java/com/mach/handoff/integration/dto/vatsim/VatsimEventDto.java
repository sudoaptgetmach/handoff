package com.mach.handoff.integration.dto.vatsim;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record VatsimEventDto(
        Long id,
        String name,
        String link,
        @JsonProperty("start_time") String startTime,
        @JsonProperty("end_time") String endTime,
        @JsonProperty("short_description") String shortDescription,
        String description,
        @JsonProperty("banner") String bannerUrl,
        List<VatsimAirportDto> airports
) {
}
