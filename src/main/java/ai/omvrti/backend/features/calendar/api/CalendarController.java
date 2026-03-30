package ai.omvrti.backend.features.calendar.api;

import ai.omvrti.backend.features.calendar.application.CalendarService;
import ai.omvrti.backend.features.auth.storage.TokenStore;
import ai.omvrti.backend.features.auth.model.TokenData;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarService service;

    public CalendarController(CalendarService service) {
        this.service = service;
    }

    private TokenData getToken() {
        TokenData token = TokenStore.get("user1");

        if (token == null || token.access_token == null) {
            throw new RuntimeException("User not authenticated. Please login.");
        }

        return token;
    }

    @GetMapping("/list")
    public Object listCalendars() throws Exception {
        TokenData token = getToken();
        return service.listCalendars("google", token.access_token);
    }

    @GetMapping("/events")
    public Object getEvents(
            @RequestParam(defaultValue = "primary") String calendarId
    ) throws Exception {
        TokenData token = getToken();
        return service.getEvents("google", token.access_token, calendarId);
    }

    @PostMapping("/events")
    public Object createEvent(
            @RequestParam(defaultValue = "primary") String calendarId,
            @RequestBody Map<String, Object> body
    ) throws Exception {
        TokenData token = getToken();
        return service.createEvent("google", token.access_token, calendarId, body);
    }

    @PostMapping("/quick-add")
    public Object quickAdd(
            @RequestParam(defaultValue = "primary") String calendarId,
            @RequestBody Map<String, String> body
    ) throws Exception {
        TokenData token = getToken();
        return service.quickAdd("google", token.access_token, calendarId, body.get("text"));
    }

    @DeleteMapping("/events/{id}")
    public Object deleteEvent(
            @RequestParam(defaultValue = "primary") String calendarId,
            @PathVariable String id
    ) throws Exception {
        TokenData token = getToken();
        service.deleteEvent("google", token.access_token, calendarId, id);
        return Map.of("success", true);
    }
}