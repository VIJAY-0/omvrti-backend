package ai.omvrti.backend.features.calendar.application.mapper;

import ai.omvrti.backend.features.calendar.domain.*;
import ai.omvrti.backend.features.calendar.integration.google.dto.*;
import ai.omvrti.backend.features.calendar.api.dto.request.CreateEventRequest;

import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class GoogleCalendarMapper {

    private static final Logger log = LoggerFactory.getLogger(GoogleCalendarMapper.class);

    public Event toDomain(GoogleEventDto dto) {

        Event event = new Event();
        event.setId(dto.getId());
        event.setSummary(dto.getSummary());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setStartDateTime(dto.getStartDateTime());
        event.setStartDate(dto.getStartDate());
        event.setEndDateTime(dto.getEndDateTime());
        event.setEndDate(dto.getEndDate());
        event.setIsSingleDay(dto.getStartDateTime()!=null);

        log.info("-------------- EVENT CREATED -----------------");
        log.info("description:   {}", dto.getDescription());
        log.info("summary:       {}", dto.getSummary());
        log.info("location:      {}", dto.getLocation());
        log.info("startDate:     {}", dto.getStartDate());
        log.info("endDate:       {}", dto.getEndDate());
        log.info("startDateTime: {}", dto.getStartDateTime());
        log.info("endDateTime:   {}", dto.getEndDateTime());
        log.info("----------------------------------------------");
        return event;
    }

    public GoogleEventDto toGoogle(CreateEventRequest request) {
        GoogleEventDto dto = new GoogleEventDto();
        dto.setSummary(request.getTitle());
        dto.setDescription(request.getDescription());
        dto.setLocation(request.getLocation());
        dto.setStartDateTime(request.getStart());
        dto.setEndDateTime(request.getEnd());
        return dto;
    }

    public Calendar toDomainCalendar(GoogleCalendarDto dto) {
        Calendar calendar = new Calendar();
        calendar.setId(dto.getId());
        calendar.setName(dto.getSummary());
        return calendar;
    }
}