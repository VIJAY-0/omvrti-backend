package ai.omvrti.backend.features.calendar.api.response;

import java.util.List;

public class CalendarListResponse {

    private List<CalendarItem> calendars;

    public CalendarListResponse(List<CalendarItem> calendars) {
        this.calendars = calendars;
    }

    public List<CalendarItem> getCalendars() {
        return calendars;
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