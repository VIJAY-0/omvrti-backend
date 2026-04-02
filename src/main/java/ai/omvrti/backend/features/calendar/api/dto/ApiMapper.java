package ai.omvrti.backend.features.calendar.api.dto;

import ai.omvrti.backend.features.calendar.domain.Calendar;
import ai.omvrti.backend.features.calendar.domain.Event;

import ai.omvrti.backend.features.calendar.api.dto.response.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ApiMapper {
        private static final Logger log = LoggerFactory.getLogger(ApiMapper.class);


    // ===================== CALENDAR =====================

    public static CalendarListResponse toCalendarListResponse(List<Calendar> domainCalendars) {
        List<CalendarListResponse.CalendarItem> items = domainCalendars.stream()
                .map(c -> new CalendarListResponse.CalendarItem(
                        c.getId(),
                        c.getName(), // name → summary
                        null // timezone not in domain
                ))
                .toList();
        return new CalendarListResponse(items);
    }

    // ===================== EVENT =====================

    public static EventResponse toEventResponse(Event event) {
        EventResponse response = new EventResponse(
                event.getId(),
                event.getSummary(),
                event.getDescription(),
                event.getLocation(),
                event.getStartDateTime(),
                event.getStartDate(),
                event.getEndDateTime(),
                event.getEndDate());
        return response;
    }

    public static EventListResponse toEventListResponse(List<Event> events) {
        List<EventResponse> responses = events.stream()
                .map(ApiMapper::toEventResponse)
                .toList();
        return new EventListResponse(responses);
    }

    public static CreateEventResponse toCreateEventResponse(Event event) {
        return new CreateEventResponse(
                event.getId(),
                "confirmed", // default
                null // not in domain
        );
    }

    public static DeleteEventResponse toDeleteEventResponse(boolean success) {
        return new DeleteEventResponse(success);
    }
}