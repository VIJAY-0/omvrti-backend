package ai.omvrti.backend.features.calendar.api;

import ai.omvrti.backend.features.calendar.application.CalendarService;
import ai.omvrti.backend.features.calendar.api.request.CreateEventRequest;
import ai.omvrti.backend.features.calendar.api.request.QuickAddRequest;
import ai.omvrti.backend.features.calendar.api.response.DeleteEventResponse;
import ai.omvrti.backend.features.auth.storage.TokenStore;
import ai.omvrti.backend.features.auth.model.TokenData;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarService service;

    public CalendarController(CalendarService service) {
        this.service = service;
    }

    private String getUser() {
        return "user1"; // replace later
    }

    private TokenData getToken(String provider) {
        TokenData token = TokenStore.get(getUser(), provider);

        if (token == null || token.access_token == null) {
            throw new RuntimeException("User not authenticated with " + provider);
        }

        return token;
    }

    @GetMapping("/{provider}/list")
    public Object listCalendars(@PathVariable String provider) throws Exception {
        TokenData token = getToken(provider);
        return service.listCalendars(provider, token.access_token);
    }

    @GetMapping("/{provider}/events")
    public Object getEvents(
            @PathVariable String provider,
            @RequestParam(defaultValue = "primary") String calendarId
    ) throws Exception {
        TokenData token = getToken(provider);
        return service.getEvents(provider, token.access_token, calendarId);
    }

    @PostMapping("/{provider}/events")
    public Object createEvent(
            @PathVariable String provider,
            @RequestParam(defaultValue = "primary") String calendarId,
            @Valid @RequestBody CreateEventRequest body
    ) throws Exception {
        TokenData token = getToken(provider);
        return service.createEvent(provider, token.access_token, calendarId, body);
    }

    @PostMapping("/{provider}/quick-add")
    public Object quickAdd(
            @PathVariable String provider,
            @RequestParam(defaultValue = "primary") String calendarId,
            @Valid @RequestBody QuickAddRequest body
    ) throws Exception {
        TokenData token = getToken(provider);
        return service.quickAdd(provider, token.access_token, calendarId, body.getText());
    }

    @DeleteMapping("/{provider}/events/{id}")
    public DeleteEventResponse deleteEvent(
            @PathVariable String provider,
            @RequestParam(defaultValue = "primary") String calendarId,
            @PathVariable String id
    ) throws Exception {
        TokenData token = getToken(provider);
        service.deleteEvent(provider, token.access_token, calendarId, id);
        return new DeleteEventResponse(true);
    }
}