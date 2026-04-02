package ai.omvrti.backend.features.calendar.api.dto.request;

import java.time.OffsetDateTime;

public class CreateEventRequest {

    private String title;
    private String description;
    private String location;
    private OffsetDateTime start;
    private OffsetDateTime end;

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public OffsetDateTime getStart() { return start; }
    public OffsetDateTime getEnd() { return end; }
}