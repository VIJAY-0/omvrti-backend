package ai.omvrti.backend.features.calendar.api;

import ai.omvrti.backend.common.responses.ApiResponse;
import ai.omvrti.backend.common.responses.CommonResponseCode;
import ai.omvrti.backend.features.calendar.application.CalendarService;
import ai.omvrti.backend.features.calendar.api.dto.request.*;
import ai.omvrti.backend.features.calendar.api.dto.response.*;
import ai.omvrti.backend.features.calendar.api.dto.ApiMapper;
import ai.omvrti.backend.features.auth.storage.TokenStore;
import ai.omvrti.backend.features.auth.model.TokenData;
import ai.omvrti.backend.features.calendar.domain.Calendar;
import ai.omvrti.backend.features.calendar.domain.Event;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarService service;

    public CalendarController(CalendarService service) {
        this.service = service;
    }

    // TODO: Replace with actual Spring Security Context
    private String getUser() {
        return "user1";
    }

    private TokenData getToken(String provider) {
        TokenData token = TokenStore.get(getUser(), provider);
        if (token == null || token.access_token == null) {
            log.warn("Unauthorized access attempt for provider: {}", provider);
            throw new RuntimeException("User not authenticated with " + provider); // TODO: Create custom UnauthorizedException
        }
        return token;
    }

    // ===================== CALENDARS =====================

    @GetMapping("/{provider}/list")
    public ResponseEntity<ApiResponse<CalendarListResponse>> listCalendars(@PathVariable String provider) throws Exception {
        TokenData token = getToken(provider);
        List<Calendar> calendars = service.listCalendars(provider, token.access_token);
        CalendarListResponse response = ApiMapper.toCalendarListResponse(calendars);
        
        return ResponseEntity.ok(ApiResponse.success(CommonResponseCode.SUCCESS, response));
    }

    // ===================== EVENTS =====================

    @GetMapping("/{provider}/events")
    public ResponseEntity<ApiResponse<EventListResponse>> getEvents(
            @PathVariable String provider,
            @RequestParam(defaultValue = "primary") String calendarId
    ) throws Exception {
        TokenData token = getToken(provider);
        List<Event> events = service.getEvents(provider, token.access_token, calendarId);
        EventListResponse response = ApiMapper.toEventListResponse(events);
        
        return ResponseEntity.ok(ApiResponse.success(CommonResponseCode.SUCCESS, response));
    }

    @PostMapping("/{provider}/events")
    public ResponseEntity<ApiResponse<CreateEventResponse>> createEvent(
            @PathVariable String provider,
            @RequestParam(defaultValue = "primary") String calendarId,
            @Valid @RequestBody CreateEventRequest body
    ) throws Exception {
        TokenData token = getToken(provider);
        Event event = service.createEvent(provider, token.access_token, calendarId, body);
        CreateEventResponse response = ApiMapper.toCreateEventResponse(event);
        
        return ResponseEntity.ok(ApiResponse.success(CommonResponseCode.SUCCESS, response));
    }

    @PostMapping("/{provider}/quick-add")
    public ResponseEntity<ApiResponse<CreateEventResponse>> quickAdd(
            @PathVariable String provider,
            @RequestParam(defaultValue = "primary") String calendarId,
            @Valid @RequestBody QuickAddRequest body
    ) throws Exception {
        TokenData token = getToken(provider);
        Event event = service.quickAdd(provider, token.access_token, calendarId, body.getText());
        CreateEventResponse response = ApiMapper.toCreateEventResponse(event);
        
        return ResponseEntity.ok(ApiResponse.success(CommonResponseCode.SUCCESS, response));
    }

    @DeleteMapping("/{provider}/events/{id}")
    public ResponseEntity<ApiResponse<DeleteEventResponse>> deleteEvent(
            @PathVariable String provider,
            @RequestParam(defaultValue = "primary") String calendarId,
            @PathVariable String id
    ) throws Exception {
        TokenData token = getToken(provider);
        service.deleteEvent(provider, token.access_token, calendarId, id);
        DeleteEventResponse response = ApiMapper.toDeleteEventResponse(true);
        
        return ResponseEntity.ok(ApiResponse.success(CommonResponseCode.SUCCESS, response));
    }
}