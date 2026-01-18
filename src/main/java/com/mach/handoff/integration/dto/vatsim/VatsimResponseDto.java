package com.mach.handoff.integration.dto.vatsim;

import java.util.List;

public record VatsimResponseDto(
        List<VatsimEventDto> data
) {
}

