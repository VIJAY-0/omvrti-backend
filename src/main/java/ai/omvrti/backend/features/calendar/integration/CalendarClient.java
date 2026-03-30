package ai.omvrti.backend.features.calendar.integration;

import ai.omvrti.backend.features.calendar.domain.CalendarEvent;

import java.util.List;

public interface CalendarClient {

    List<CalendarEvent> getEvents(String accessToken, String calendarId) throws Exception;

    Object listCalendars(String accessToken) throws Exception;

    Object createEvent(String accessToken, String calendarId, Object body) throws Exception;

    Object quickAdd(String accessToken, String calendarId, String text) throws Exception;

    void deleteEvent(String accessToken, String calendarId, String eventId) throws Exception;
}