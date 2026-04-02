package ai.omvrti.backend.features.calendar.domain;


import java.time.OffsetDateTime;
import java.time.LocalDate;

public class Event {
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

    // getters & setters
    public boolean isSingleDay(){return isSingleDay;}
    public void setIsSingleDay(boolean state){this.isSingleDay = state;}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public OffsetDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(OffsetDateTime startDateTime) { this.startDateTime = startDateTime; }

    public OffsetDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(OffsetDateTime endDateTime) { this.endDateTime = endDateTime; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}