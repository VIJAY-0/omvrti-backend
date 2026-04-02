package ai.omvrti.backend.features.calendar.api;

import ai.omvrti.backend.features.calendar.application.CalendarService;
import ai.omvrti.backend.features.calendar.api.dto.request.*;
import ai.omvrti.backend.features.calendar.api.dto.response.*;
import ai.omvrti.backend.features.calendar.api.dto.ApiMapper;

import ai.omvrti.backend.features.auth.storage.TokenStore;
import ai.omvrti.backend.features.auth.model.TokenData;
import ai.omvrti.backend.features.calendar.domain.*;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // ===================== CALENDARS =====================

    @GetMapping("/{provider}/list")
    public CalendarListResponse listCalendars(@PathVariable String provider) throws Exception {
        TokenData token = getToken(provider);

        List<Calendar> calendars =
                service.listCalendars(provider, token.access_token);

        return ApiMapper.toCalendarListResponse(calendars);
    }

    // ===================== EVENTS =====================

    @GetMapping("/{provider}/events")
    public EventListResponse getEvents(
            @PathVariable String provider,
            @RequestParam(defaultValue = "primary") String calendarId
    ) throws Exception {
        TokenData token = getToken(provider);
        List<Event> events = service.getEvents(provider, token.access_token, calendarId);
        return ApiMapper.toEventListResponse(events);
    }

    @PostMapping("/{provider}/events")
    public CreateEventResponse createEvent(
            @PathVariable String provider,
            @RequestParam(defaultValue = "primary") String calendarId,
            @Valid @RequestBody CreateEventRequest body
    ) throws Exception {
        TokenData token = getToken(provider);
        Event event =service.createEvent(provider, token.access_token, calendarId, body);
        return ApiMapper.toCreateEventResponse(event);
    }

    @PostMapping("/{provider}/quick-add")
    public CreateEventResponse quickAdd(
            @PathVariable String provider,
            @RequestParam(defaultValue = "primary") String calendarId,
            @Valid @RequestBody QuickAddRequest body
    ) throws Exception {
        TokenData token = getToken(provider);
        Event event = service.quickAdd(provider, token.access_token, calendarId, body.getText());
        return ApiMapper.toCreateEventResponse(event);
    }

    @DeleteMapping("/{provider}/events/{id}")
    public DeleteEventResponse deleteEvent(
            @PathVariable String provider,
            @RequestParam(defaultValue = "primary") String calendarId,
            @PathVariable String id
    ) throws Exception {
        TokenData token = getToken(provider);
        service.deleteEvent(provider, token.access_token, calendarId, id);
        return ApiMapper.toDeleteEventResponse(true);
    }
}