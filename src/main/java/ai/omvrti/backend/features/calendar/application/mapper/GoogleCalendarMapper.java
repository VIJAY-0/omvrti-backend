package ai.omvrti.backend.features.calendar.application.mapper;

import ai.omvrti.backend.features.calendar.api.request.CreateEventRequest;
import ai.omvrti.backend.features.calendar.api.response.*;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component("googleCalendarMapper")
public class GoogleCalendarMapper implements CalendarMapper {

    @Override
    public CalendarListResponse mapCalendars(Map<String, Object> raw) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) raw.get("items");

        List<CalendarListResponse.CalendarItem> calendars = items.stream()
                .map(item -> new CalendarListResponse.CalendarItem(
                        (String) item.get("id"),
                        (String) item.get("summary"),
                        (String) item.get("timeZone")))
                .collect(Collectors.toList());

        return new CalendarListResponse(calendars);
    }

    @Override
    public EventListResponse mapEvents(List<Map<String, Object>> rawEvents) {
        List<EventResponse> events = rawEvents.stream()
                .map(this::mapEvent)
                .collect(Collectors.toList());

        return new EventListResponse(events);
    }

    @Override
    public EventResponse mapEvent(Map<String, Object> rawEvent) {

        Map<String, Object> start = (Map<String, Object>) rawEvent.get("start");
        Map<String, Object> end = (Map<String, Object>) rawEvent.get("end");

        System.err.println("GoogleCalendarMapper:: mapEvent");

        if(start.get("dateTime")!=null)
            System.err.println("GoogleCalendarMapper:: 1 start :: "+start.get("dateTime").toString());
        if(start.get("date")!=null)
        System.err.println("GoogleCalendarMapper:: 2 start :: "+start.get("date").toString());
    
    String startValue = null;
    String endValue = null;
    
    if (start != null) {
        startValue = start.get("dateTime") != null
        ? start.get("dateTime").toString()
        : (start.get("date") != null ? start.get("date").toString() : null);
    }
    
    if (end != null) {
        endValue = end.get("dateTime") != null
        ? end.get("dateTime").toString()
        : (end.get("date") != null ? end.get("date").toString() : null);
    }
    
    
    System.err.println("GoogleCalendarMapper:: 3 start :: "+startValue);
    System.err.println("GoogleCalendarMapper:: 4 start :: "+endValue);
    if (endValue == null) endValue = startValue;

        return new EventResponse(
                (String) rawEvent.get("id"),
                (String) rawEvent.get("summary"),
                (String) rawEvent.get("description"),
                parseDate(startValue),
                parseDate(endValue),
                (String) start.get("timeZone"));
    }

    @Override
    public CreateEventResponse mapCreateEvent(Map<String, Object> raw) {
        return new CreateEventResponse(
                (String) raw.get("id"),
                (String) raw.get("status"),
                (String) raw.get("htmlLink"));
    }

    @Override
    public Map<String, Object> toProviderCreateEvent(CreateEventRequest request) {
        Map<String, Object> body = new HashMap<>();

        body.put("summary", request.getSummary());
        body.put("description", request.getDescription());

        Map<String, Object> start = new HashMap<>();
        start.put("dateTime", request.getStart().toString());
        start.put("timeZone", request.getTimeZone());

        Map<String, Object> end = new HashMap<>();
        end.put("dateTime", request.getEnd().toString());
        end.put("timeZone", request.getTimeZone());

        body.put("start", start);
        body.put("end", end);

        return body;
    }

    // 🔥 FIXES YOUR DATE ERROR
    private OffsetDateTime parseDate(String value) {
        if (value == null)
            return null;
        try {
            return OffsetDateTime.parse(value);
        } catch (Exception e) {
            // fallback for missing seconds
            return OffsetDateTime.parse(value + ":00+00:00");
        }
    }
}