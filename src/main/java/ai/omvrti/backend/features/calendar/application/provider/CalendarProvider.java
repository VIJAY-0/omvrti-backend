package ai.omvrti.backend.features.calendar.application.provider;

import ai.omvrti.backend.features.calendar.api.request.CreateEventRequest;
import ai.omvrti.backend.features.calendar.api.response.*;

public interface CalendarProvider {

    CalendarListResponse listCalendars(String accessToken) throws Exception;

    EventListResponse getEvents(String accessToken, String calendarId) throws Exception;

    CreateEventResponse createEvent(
            String accessToken,
            String calendarId,
            CreateEventRequest request
    ) throws Exception;

    EventResponse quickAdd(
            String accessToken,
            String calendarId,
            String text
    ) throws Exception;

    void deleteEvent(
            String accessToken,
            String calendarId,
            String eventId
    ) throws Exception;
}