package ai.omvrti.backend.features.calendar.integration;

import java.util.List;
import ai.omvrti.backend.features.calendar.integration.google.dto.*;

public interface CalendarClient {

    List<GoogleCalendarDto> listCalendars(String accessToken) throws Exception;

    List<GoogleEventDto> getEvents(String accessToken, String calendarId) throws Exception;

    GoogleEventDto createEvent(String accessToken, String calendarId, GoogleEventDto event) throws Exception;

    GoogleEventDto quickAdd(String accessToken, String calendarId, String text) throws Exception;

    void deleteEvent(String accessToken, String calendarId, String eventId) throws Exception;
}