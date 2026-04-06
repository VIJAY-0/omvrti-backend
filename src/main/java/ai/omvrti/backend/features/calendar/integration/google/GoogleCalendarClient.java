package ai.omvrti.backend.features.calendar.integration.google;

import ai.omvrti.backend.features.calendar.integration.CalendarClient;
import ai.omvrti.backend.features.calendar.integration.google.dto.*;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class GoogleCalendarClient implements CalendarClient {
        private static final Logger log = LoggerFactory.getLogger(GoogleCalendarClient.class);

        private Calendar getClient(String accessToken) throws Exception {
                return new Calendar.Builder(
                                GoogleNetHttpTransport.newTrustedTransport(),
                                GsonFactory.getDefaultInstance(),
                                request -> request.getHeaders().setAuthorization("Bearer " + accessToken))
                                .setApplicationName("calendar-app")
                                .build();
        }

        @Override
        public List<GoogleCalendarDto> listCalendars(String accessToken) throws Exception {
                List<CalendarListEntry> items = getClient(accessToken)
                                .calendarList()
                                .list()
                                .execute()
                                .getItems();
                List<GoogleCalendarDto> result = new ArrayList<>();
                for (CalendarListEntry item : items) {
                        GoogleCalendarDto dto = new GoogleCalendarDto();
                        dto.setId(item.getId());
                        dto.setSummary(item.getSummary());
                        result.add(dto);
                }
                return result;
        }

        @Override
        public List<GoogleEventDto> getEvents(String accessToken, String calendarId) throws Exception {
                Events response = getClient(accessToken)
                                .events()
                                .list(calendarId)
                                .setMaxResults(50)
                                .setSingleEvents(true)
                                .setOrderBy("startTime")
                                .execute();
                List<GoogleEventDto> result = new ArrayList<>();
                for (Event item : response.getItems()) {
                        GoogleEventDto dto = new GoogleEventDto();
                        dto.setId(item.getId());
                        dto.setSummary(item.getSummary());
                        dto.setDescription(item.getDescription());
                        dto.setLocation(item.getLocation());
                        if (item.getStart() != null) {
                                if (item.getStart().getDateTime() != null) {
                                        dto.setStartDateTime( OffsetDateTime.parse(item.getStart().getDateTime().toString()));
                                }
                                else if (item.getStart().getDate() != null) {
                                        dto.setStartDate(LocalDate.parse(item.getStart().getDate().toString()));
                                }
                        }
                        if (item.getEnd() != null) {
                                if (item.getEnd().getDateTime() != null) {
                                        dto.setEndDateTime(OffsetDateTime.parse(item.getEnd().getDateTime().toString()));
                                } else if (item.getEnd().getDate() != null) {
                                        dto.setEndDate(LocalDate.parse(item.getEnd().getDate().toString()));
                                }
                        }
                        result.add(dto);
                }
                return result;
        }

        @Override
        public GoogleEventDto createEvent(String accessToken, String calendarId, GoogleEventDto input)
                        throws Exception {
                Event event = new Event()
                                .setSummary(input.getSummary())
                                .setDescription(input.getDescription())
                                .setLocation(input.getLocation());
                event.setStart(new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(input.getStartDateTime().toString())));
                event.setEnd(new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(input.getEndDateTime().toString())));
                Event created = getClient(accessToken)
                                .events()
                                .insert(calendarId, event)
                                .execute();
                GoogleEventDto dto = new GoogleEventDto();
                dto.setId(created.getId());
                dto.setSummary(created.getSummary());
                return dto;
        }

        @Override
        public GoogleEventDto quickAdd(String accessToken, String calendarId, String text) throws Exception {
                Event event = getClient(accessToken)
                                .events()
                                .quickAdd(calendarId, text)
                                .execute();
                GoogleEventDto dto = new GoogleEventDto();
                dto.setId(event.getId());
                dto.setSummary(event.getSummary());
                return dto;
        }

        @Override
        public void deleteEvent(String accessToken, String calendarId, String eventId) throws Exception {
                getClient(accessToken)
                                .events()
                                .delete(calendarId, eventId)
                                .execute();
        }
}