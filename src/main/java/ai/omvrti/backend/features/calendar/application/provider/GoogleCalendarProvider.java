package ai.omvrti.backend.features.calendar.application.provider;

import ai.omvrti.backend.features.calendar.integration.google.GoogleCalendarClient;
import ai.omvrti.backend.features.calendar.integration.google.dto.*;
import ai.omvrti.backend.features.calendar.application.mapper.GoogleCalendarMapper;
import ai.omvrti.backend.features.calendar.domain.*;
import ai.omvrti.backend.features.calendar.api.dto.request.CreateEventRequest;

import org.springframework.stereotype.Component;

import java.util.List;

@Component("googleCalendar")
public class GoogleCalendarProvider implements CalendarProvider {

    private final GoogleCalendarClient client;
    private final GoogleCalendarMapper mapper;

    public GoogleCalendarProvider(GoogleCalendarClient client, GoogleCalendarMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public List<Calendar> listCalendars(String accessToken) throws Exception {
        List<GoogleCalendarDto> calendars = client.listCalendars(accessToken);
        List<Calendar> domainCalendars = calendars.stream()
                .map(mapper::toDomainCalendar)
                .toList();
               return domainCalendars;
    }

    @Override
    public List<Event> getEvents(String accessToken, String calendarId) throws Exception {
        List<GoogleEventDto> googleEvents = client.getEvents(accessToken, calendarId);
        List<Event> events = googleEvents.stream()
                .map(mapper::toDomain)
                .toList();
        return events;
    }

    @Override
    public Event createEvent(
            String accessToken,
            String calendarId,
            CreateEventRequest request
    ) throws Exception {

        GoogleEventDto googleDto = mapper.toGoogle(request);
        GoogleEventDto created = client.createEvent(accessToken, calendarId, googleDto);
        Event event = mapper.toDomain(created);
        return event;
    }

    @Override
    public Event quickAdd(
            String accessToken,
            String calendarId,
            String text
    ) throws Exception {
        GoogleEventDto created = client.quickAdd(accessToken, calendarId, text);
        Event event = mapper.toDomain(created);
        return event;
    }

    @Override
    public void deleteEvent(String accessToken, String calendarId, String eventId) throws Exception {
        client.deleteEvent(accessToken, calendarId, eventId);
    }
}