package ai.omvrti.backend.features.calendar.application.provider;

import ai.omvrti.backend.features.calendar.api.request.CreateEventRequest;
import ai.omvrti.backend.features.calendar.api.response.*;
import ai.omvrti.backend.features.calendar.application.mapper.CalendarMapper;
import ai.omvrti.backend.features.calendar.integration.google.GoogleCalendarClient;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("googleCalendar")
public class GoogleCalendarProvider implements CalendarProvider {

    private final GoogleCalendarClient client;
    private final CalendarMapper mapper;

    public GoogleCalendarProvider(
            GoogleCalendarClient client,
            Map<String, CalendarMapper> mappers) {
        this.client = client;
        this.mapper = mappers.get("googleCalendarMapper");
    }

    @Override
    public CalendarListResponse listCalendars(String accessToken) throws Exception {
        Map<String, Object> raw = (Map<String, Object>) client.listCalendars(accessToken);
        return mapper.mapCalendars(raw);
    }

    @Override
    public EventListResponse getEvents(String accessToken, String calendarId) throws Exception {
        System.err.println("GoogleCalendarProvider:: getEvent");
        Map<String, Object> raw = (Map<String, Object>) client.getEvents(accessToken, calendarId);
        List<Map<String, Object>> items = (List<Map<String, Object>>) raw.get("items");
        System.err.println("GoogleCalendarProvider:: getEvent");
        return mapper.mapEvents(items);
    }

    @Override
    public CreateEventResponse createEvent(
            String accessToken,
            String calendarId,
            CreateEventRequest request) throws Exception {

        Map<String, Object> body = mapper.toProviderCreateEvent(request);
        Map<String, Object> raw = (Map<String, Object>) client.createEvent(accessToken, calendarId, body);

        return mapper.mapCreateEvent(raw);
    }

    @Override
    public EventResponse quickAdd(
            String accessToken,
            String calendarId,
            String text) throws Exception {

        Map<String, Object> raw = (Map<String, Object>) client.quickAdd(accessToken, calendarId, text);
        return mapper.mapEvent(raw);
    }

    @Override
    public void deleteEvent(String accessToken, String calendarId, String eventId) throws Exception {
        client.deleteEvent(accessToken, calendarId, eventId);
    }
}