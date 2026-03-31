package ai.omvrti.backend.features.calendar.api.response;

import java.time.OffsetDateTime;

public class EventResponse {

    private String id;
    private String summary;
    private String description;

    private OffsetDateTime start;
    private OffsetDateTime end;

    private String timeZone;

    public EventResponse(
            String id,
            String summary,
            String description,
            OffsetDateTime start,
            OffsetDateTime end,
            String timeZone
    ) {
        this.id = id;
        this.summary = summary;
        this.description = description;
        this.start = start;
        this.end = end;
        this.timeZone = timeZone;
    }

    public String getId() { return id; }
    public String getSummary() { return summary; }
    public String getDescription() { return description; }
    public OffsetDateTime getStart() { return start; }
    public OffsetDateTime getEnd() { return end; }
    public String getTimeZone() { return timeZone; }
}