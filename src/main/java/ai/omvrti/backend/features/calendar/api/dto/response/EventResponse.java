package ai.omvrti.backend.features.calendar.api.dto.response;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class EventResponse {

    private String id;
    private String summary;
    private String description;
    private String location;
    private OffsetDateTime startDateTime;
    private OffsetDateTime endDateTime;

    // All-day event
    private LocalDate startDate;
    private LocalDate endDate;

    private boolean isSingleDay; 

    public EventResponse(
            String id,
            String summary,
            String description,
            String location,
            OffsetDateTime startDateTime,
            LocalDate startDate,
            OffsetDateTime endDateTime,
            LocalDate endDate
    ) {
        this.id = id;
        this.summary = summary;
        this.description = description;
        this.location = location;
        this.endDate = endDate;
        this.endDateTime = endDateTime;
        this.startDateTime = startDateTime;
        this.startDate = startDate;
    }

    public String getId() { return id; }
    public String getSummary() { return summary; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public OffsetDateTime getStartDateTime() { return startDateTime; }
    public OffsetDateTime getEndDateTime() { return endDateTime; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
}