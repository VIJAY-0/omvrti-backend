package ai.omvrti.backend.features.calendar.api.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public class CreateEventRequest {

    @NotBlank(message = "Summary is required")
    private String summary;

    private String description;

    @NotNull(message = "Start time is required")
    private OffsetDateTime start;

    @NotNull(message = "End time is required")
    private OffsetDateTime end;

    private String timeZone;

    public CreateEventRequest() {
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public OffsetDateTime getStart() {
        return start;
    }

    public OffsetDateTime getEnd() {
        return end;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStart(OffsetDateTime start) {
        this.start = start;
    }

    public void setEnd(OffsetDateTime end) {
        this.end = end;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @AssertTrue(message = "End time must be after start time")
    public boolean isValidTimeRange() {
        if (start == null || end == null)
            return true;
        return end.isAfter(start);
    }

}