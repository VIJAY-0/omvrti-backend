package ai.omvrti.backend.features.calendar.application;

import ai.omvrti.backend.features.calendar.application.provider.CalendarProvider;
import ai.omvrti.backend.features.calendar.api.request.CreateEventRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CalendarService {

    private final Map<String, CalendarProvider> providers;

    public CalendarService(Map<String, CalendarProvider> providers) {
        this.providers = providers;
    }

    private CalendarProvider getProvider(String provider) {

    String beanKey;

    switch (provider.toLowerCase()) {
        case "google":
            beanKey = "googleCalendar";
            break;
        case "microsoft":
            beanKey = "microsoftCalendar";
            break;
        default:
            throw new IllegalArgumentException("Unsupported provider: " + provider);
    }

    CalendarProvider p = providers.get(beanKey);

    if (p == null) {
        throw new IllegalStateException("Provider not configured: " + beanKey);
    }

    return p;
}

    public Object listCalendars(String provider, String token) throws Exception {
        return getProvider(provider).listCalendars(token);
    }

    public Object getEvents(String provider, String token, String calendarId) throws Exception {
        return getProvider(provider).getEvents(token, calendarId);
    }

    public Object createEvent(String provider, String token, String calendarId, CreateEventRequest req) throws Exception {
        return getProvider(provider).createEvent(token, calendarId, req);
    }

    public Object quickAdd(String provider, String token, String calendarId, String text) throws Exception {
        return getProvider(provider).quickAdd(token, calendarId, text);
    }

    public void deleteEvent(String provider, String token, String calendarId, String id) throws Exception {
        getProvider(provider).deleteEvent(token, calendarId, id);
    }
}