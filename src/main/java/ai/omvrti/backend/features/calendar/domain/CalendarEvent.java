package ai.omvrti.backend.features.calendar.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalendarEvent {
    private String id;
    private String summary;
    private String description;
    private String location;
    private String start;
    private String end;
    private String type;
}