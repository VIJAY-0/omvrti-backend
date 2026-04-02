package ai.omvrti.backend.features.calendar.api.dto.response;

import ai.omvrti.backend.features.calendar.domain.Event;

public class CreateEventResponse {

    private String id;
    private String status;
    private String htmlLink;

    public CreateEventResponse(String id, String status, String htmlLink) {
        this.id = id;
        this.status = status;
        this.htmlLink = htmlLink;
    }

    public String getId() { return id; }
    public String getStatus() { return status; }
    public String getHtmlLink() { return htmlLink; }

    // ✅ ADD THIS
    public static CreateEventResponse from(Event event) {
        return new CreateEventResponse(
                event.getId(),
                "confirmed",   // default (Google usually returns this)
                null           // not available in domain
        );
    }
}