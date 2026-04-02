package ai.omvrti.backend.features.calendar.api.dto.response;

import ai.omvrti.backend.features.calendar.domain.Calendar;

import java.util.List;

public class CalendarListResponse {

    private List<CalendarItem> calendars;

    public CalendarListResponse(List<CalendarItem> calendars) {
        this.calendars = calendars;
    }

    public List<CalendarItem> getCalendars() {
        return calendars;
    }

    // ✅ ADD THIS
    public static CalendarListResponse from(List<Calendar> domainCalendars) {
        List<CalendarItem> items = domainCalendars.stream()
                .map(c -> new CalendarItem(
                        c.getId(),
                        c.getName(),   // mapping name → summary
                        null           // timezone missing in domain
                ))
                .toList();

        return new CalendarListResponse(items);
    }

    public static class CalendarItem {
        private String id;
        private String summary;
        private String timeZone;

        public CalendarItem(String id, String summary, String timeZone) {
            this.id = id;
            this.summary = summary;
            this.timeZone = timeZone;
        }

        public String getId() { return id; }
        public String getSummary() { return summary; }
        public String getTimeZone() { return timeZone; }
    }
}