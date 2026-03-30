package ai.omvrti.backend.features.calendar.application;

import ai.omvrti.backend.features.calendar.integration.CalendarClient;
import ai.omvrti.backend.features.calendar.integration.google.GoogleCalendarClient;
import org.springframework.stereotype.Service;

@Service
public class CalendarService {

    private final CalendarClient googleClient = new GoogleCalendarClient();

    public Object listCalendars(String provider, String token) throws Exception {
        return googleClient.listCalendars(token);
    }

    public Object getEvents(String provider, String token, String calendarId) throws Exception {
        return googleClient.getEvents(token, calendarId);
    }

    public Object createEvent(String provider, String token, String calendarId, Object body) throws Exception {
        return googleClient.createEvent(token, calendarId, body);
    }

    public Object quickAdd(String provider, String token, String calendarId, String text) throws Exception {
        return googleClient.quickAdd(token, calendarId, text);
    }

    public void deleteEvent(String provider, String token, String calendarId, String eventId) throws Exception {
        googleClient.deleteEvent(token, calendarId, eventId);
    }
}