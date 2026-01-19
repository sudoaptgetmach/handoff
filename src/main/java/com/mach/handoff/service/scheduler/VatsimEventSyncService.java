package com.mach.handoff.service.scheduler;

import com.mach.handoff.domain.enums.events.EventStatus;
import com.mach.handoff.domain.events.Event;
import com.mach.handoff.integration.dto.vatsim.VatsimAirportDto;
import com.mach.handoff.integration.dto.vatsim.VatsimEventDto;
import com.mach.handoff.integration.dto.vatsim.VatsimResponseDto;
import com.mach.handoff.repository.EventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.ZonedDateTime;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class VatsimEventSyncService {

    private final EventRepository repository;
    private final RestClient restClient;

    public VatsimEventSyncService(EventRepository repository) {
        this.repository = repository;
        this.restClient = RestClient.create();
    }

    @Scheduled(cron = "0 0 */1 * * *")
    @Transactional
    public void syncEvents() {
        System.out.println("Iniciando sincronização com a VATSIM...");
        try {
            var response = restClient.get()
                    .uri("https://my.vatsim.net/api/v2/events/view/division/BRZ")
                    .retrieve()
                    .body(VatsimResponseDto.class);

            if (response == null || response.data() == null) return;

            for (var dto : response.data()) {
                var existingEvent = repository.findByVatsimId(dto.id());

                if (existingEvent.isPresent()) {
                    updateEvent(existingEvent.get(), dto);
                } else {
                    createEvent(dto);
                }
            }

            System.out.println("Sincronização concluída com sucesso.");

        } catch (Exception e) {
            System.err.println("Erro ao sincronizar eventos: " + e.getMessage());
            Logger.getGlobal().severe(e.getMessage());
        }
    }

//    @EventListener(ApplicationReadyEvent.class)
//    public void testarNaInicializacao() {
//        syncEvents();
//    }

    private void createEvent(VatsimEventDto dto) {
        Event newEvent = Event.builder()
                .vatsimId(dto.id())
                .name(dto.name())
                .description(dto.description())
                .bannerUrl(dto.bannerUrl())
                .link(dto.link())
                .startTime(ZonedDateTime.parse(dto.startTime()).toLocalDateTime())
                .endTime(ZonedDateTime.parse(dto.endTime()).toLocalDateTime())
                .airports(dto.airports().stream().map(VatsimAirportDto::icao).collect(Collectors.toSet()))
                .status(EventStatus.DRAFT)
                .build();

        repository.save(newEvent);
        System.out.println("Evento criado: " + dto.name());
    }

    private void updateEvent(Event event, VatsimEventDto dto) {
        event.setName(dto.name());
        event.setBannerUrl(dto.bannerUrl());
        event.setStartTime(ZonedDateTime.parse(dto.startTime()).toLocalDateTime());
        event.setEndTime(ZonedDateTime.parse(dto.endTime()).toLocalDateTime());

        event.setAirports(dto.airports().stream().map(VatsimAirportDto::icao).collect(Collectors.toSet()));

        repository.save(event);
        System.out.println("Evento atualizado: " + dto.name());
    }
}