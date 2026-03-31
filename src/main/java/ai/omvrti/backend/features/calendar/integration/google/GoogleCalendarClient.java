package ai.omvrti.backend.features.calendar.integration.google;

import ai.omvrti.backend.features.calendar.domain.CalendarEvent;
import ai.omvrti.backend.features.calendar.integration.CalendarClient;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class GoogleCalendarClient implements CalendarClient {

        private Calendar getClient(String accessToken) throws Exception {
                return new Calendar.Builder(
                                GoogleNetHttpTransport.newTrustedTransport(),
                                GsonFactory.getDefaultInstance(),
                                request -> request.getHeaders().setAuthorization("Bearer " + accessToken))
                                .setApplicationName("calendar-app").build();
        }

        @Override
        public Object listCalendars(String accessToken) throws Exception {
                List<?> items = getClient(accessToken)
                                .calendarList()
                                .list()
                                .execute()
                                .getItems();
                Map<String, Object> result = new HashMap<>();
                result.put("items", items);
                return result;
        }

        @Override
        public Object getEvents(String accessToken, String calendarId) throws Exception {
                Events response = getClient(accessToken).events().list(calendarId)
                                .setMaxResults(50)
                                .setSingleEvents(true)
                                .setOrderBy("startTime")
                                .execute();
                List<Map<String, Object>> items = new ArrayList<>();
                for (Event item : response.getItems()) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", item.getId());
                        map.put("summary", item.getSummary());
                        map.put("description", item.getDescription());
                        map.put("location", item.getLocation());
                        Map<String, Object> startMap = new HashMap<>();
                        if (item.getStart() != null) {
                                if (item.getStart().getDateTime() != null) {
                                        startMap.put("dateTime", item.getStart().getDateTime().toString());
                                } else if (item.getStart().getDate() != null) {
                                        startMap.put("date", item.getStart().getDate().toString());
                                }
                        }
                        map.put("start", startMap);
                        map.put("end", item.getEnd().getDateTime());
                        items.add(map);
                }
                Map<String, Object> result = new HashMap<>();
                result.put("items", items);
                return result;
        }

        private String extractType(String summary) {
                if (summary == null)
                        return "Other";
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
                Event created = getClient(accessToken)
                                .events()
                                .insert(calendarId, event)
                                .execute();
                Map<String, Object> result = new HashMap<>();
                result.put("id", created.getId());
                result.put("summary", created.getSummary());
                return result;
        }

        @Override
        public Object quickAdd(String accessToken, String calendarId, String text) throws Exception {
                Event event = getClient(accessToken)
                                .events()
                                .quickAdd(calendarId, text)
                                .execute();
                Map<String, Object> result = new HashMap<>();
                result.put("id", event.getId());
                result.put("summary", event.getSummary());
                return result;
        }

        @Override
        public void deleteEvent(String accessToken, String calendarId, String eventId) throws Exception {
                getClient(accessToken)
                                .events()
                                .delete(calendarId, eventId)
                                .execute();
        }
}