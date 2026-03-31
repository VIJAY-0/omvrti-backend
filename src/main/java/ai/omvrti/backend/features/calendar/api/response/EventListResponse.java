package ai.omvrti.backend.features.calendar.api.response;

import java.util.List;

public class EventListResponse {

    private List<EventResponse> events;

    public EventListResponse(List<EventResponse> events) {
        this.events = events;
    }

    public List<EventResponse> getEvents() {
        return events;
    }
}