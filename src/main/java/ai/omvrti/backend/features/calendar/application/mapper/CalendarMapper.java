package ai.omvrti.backend.features.calendar.application.mapper;

import ai.omvrti.backend.features.calendar.api.request.CreateEventRequest;
import ai.omvrti.backend.features.calendar.api.response.*;

import java.util.List;
import java.util.Map;

public interface CalendarMapper {

    CalendarListResponse mapCalendars(Map<String, Object> raw);

    EventListResponse mapEvents(List<Map<String, Object>> rawEvents);

    EventResponse mapEvent(Map<String, Object> rawEvent);

    CreateEventResponse mapCreateEvent(Map<String, Object> raw);

    Map<String, Object> toProviderCreateEvent(CreateEventRequest request);
}