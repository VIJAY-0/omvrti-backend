package ai.omvrti.backend.features.calendar.integration.google;

import ai.omvrti.backend.features.calendar.domain.CalendarEvent;
import ai.omvrti.backend.features.calendar.integration.CalendarClient;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class GoogleCalendarClient implements CalendarClient {

    private Calendar getClient(String accessToken) throws Exception {
        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                request -> request.getHeaders().setAuthorization("Bearer " + accessToken)
        ).setApplicationName("calendar-app").build();
    }

    @Override
    public Object listCalendars(String accessToken) throws Exception {
        return getClient(accessToken)
                .calendarList()
                .list()
                .execute()
                .getItems();
    }

    @Override
    public List<CalendarEvent> getEvents(String accessToken, String calendarId) throws Exception {

        Events response = getClient(accessToken).events().list(calendarId)
                .setMaxResults(50)
                .setSingleEvents(true)
                .setOrderBy("startTime")
                .execute();

        List<CalendarEvent> result = new ArrayList<>();

        for (Event item : response.getItems()) {
            result.add(CalendarEvent.builder()
                    .id(item.getId())
                    .summary(item.getSummary() != null ? item.getSummary() : "(No title)")
                    .description(item.getDescription())
                    .location(item.getLocation())
                    .start(item.getStart().getDateTime() != null ? item.getStart().getDateTime().toString() : null)
                    .end(item.getEnd().getDateTime() != null ? item.getEnd().getDateTime().toString() : null)
                    .type(extractType(item.getSummary()))
                    .build());
        }

        return result;
    }

    private String extractType(String summary) {
        if (summary == null) return "Other";
        var matcher = Pattern.compile("\\[(.*?)\\]").matcher(summary);
        return matcher.find() ? matcher.group(1) : "Other";
    }

    @Override
    public Object createEvent(String accessToken, String calendarId, Object body) throws Exception {

        Map<String, Object> map = (Map<String, Object>) body;

        Event event = new Event()
                .setSummary((String) map.get("summary"))
                .setDescription((String) map.get("description"))
                .setLocation((String) map.get("location"));

        event.setStart(new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime((String) map.get("start"))));

        event.setEnd(new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime((String) map.get("end"))));

        return getClient(accessToken)
                .events()
                .insert(calendarId, event)
                .execute();
    }

    @Override
    public Object quickAdd(String accessToken, String calendarId, String text) throws Exception {
        return getClient(accessToken)
                .events()
                .quickAdd(calendarId, text)
                .execute();
    }

    @Override
    public void deleteEvent(String accessToken, String calendarId, String eventId) throws Exception {
        getClient(accessToken)
                .events()
                .delete(calendarId, eventId)
                .execute();
    }
}