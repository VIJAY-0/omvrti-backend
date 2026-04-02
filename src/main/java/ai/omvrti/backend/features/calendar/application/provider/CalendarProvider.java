package ai.omvrti.backend.features.calendar.application.provider;

import ai.omvrti.backend.features.calendar.api.dto.request.*;
import ai.omvrti.backend.features.calendar.domain.*;
import java.util.List;

public interface CalendarProvider {

        List<Calendar> listCalendars(
                        String accessToken) throws Exception;

        List<Event> getEvents(
                        String accessToken,
                        String calendarId) throws Exception;

        Event createEvent(
                        String accessToken,
                        String calendarId,
                        CreateEventRequest request) throws Exception;

        Event quickAdd(
                        String accessToken,
                        String calendarId,
                        String text) throws Exception;

        void deleteEvent(
                        String accessToken,
                        String calendarId,
                        String eventId) throws Exception;
}