package greencity.controller;

import greencity.config.SecurityConfig;
import greencity.converters.UserArgumentResolver;
import greencity.dto.event.CreateEventDtoResponse;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.user.UserVO;
import greencity.service.EventService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.test.context.support.WithMockUser;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.List;

import static greencity.ModelUtils.getUserVO;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ContextConfiguration
@Import(SecurityConfig.class)
class EventControllerTest {
    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EventController eventController;

    private final Principal principal = () -> "test@gmail.com";

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController)
                .setCustomArgumentResolvers(new UserArgumentResolver(userService, modelMapper))
                .build();
    }

    @Test
    void createEvent_Success() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(userVO, UserVO.class)).thenReturn(userVO);

        String createEventDtoJson = """
            {
              "title": "Event Title",
              "description": "This is a valid event description with more than 20 characters.",
              "mainImage": "main-image.jpg",
              "open": true,
              "dates": [
                {
                  "startDateTime": "2025-05-01T10:00:00Z",
                  "endDateTime": "2025-05-01T12:00:00Z",
                  "location": "Lviv",
                  "onlineLink": null,
                  "allDay": false
                }
              ],
              "tags": ["Еко"],
              "online": false,
              "initiativeTypes": ["EDUCATIONAL"]
            }
        """;

        MockMultipartFile jsonPart = new MockMultipartFile(
                "event",
                "createEventDto.json",
                "application/json",
                createEventDtoJson.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile image = new MockMultipartFile(
                "images", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "dummy image content".getBytes()
        );

        CreateEventDtoResponse response = CreateEventDtoResponse.builder()
                .eventId(1L)
                .title("Event Title")
                .description("This is a valid event description with more than 20 characters.")
                .open(true)
                .tags(List.of("Eco"))
                .dates(List.of(
                        new EventDateLocationDto(
                                ZonedDateTime.parse("2025-05-01T10:00:00Z"),
                                ZonedDateTime.parse("2025-05-01T12:00:00Z"),
                                "Lviv",
                                null,
                                false
                        )
                ))
                .images(List.of("image.jpg"))
                .createdDateTime(ZonedDateTime.now())
                .build();


        when(eventService.createEvent(any(), any(), any())).thenReturn(response);

        mockMvc.perform(multipart("/events/create/")
                        .file(jsonPart)
                        .file(image)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventId").value(1L))
                .andExpect(jsonPath("$.title").value("Event Title"))
                .andExpect(jsonPath("$.tags[0]").value("Eco"))
                .andExpect(jsonPath("$.open").value(true))
                .andExpect(jsonPath("$.dates[0].location").value("Lviv"))
                .andExpect(jsonPath("$.images[0]").value("image.jpg"))
                .andExpect(jsonPath("$.createdDateTime").exists());

        verify(eventService).createEvent(any(), any(), eq(userVO));
    }

    @Test
    void createEvent_BadRequest_WhenTitleIsBlank() throws Exception {
        String invalidJson = """
                {
                  "title": "  ",
                  "description": "Valid description with enough characters.",
                  "mainImage": "img.jpg",
                  "open": true,
                  "dates": [{
                    "startDateTime": "2025-05-01T10:00:00Z",
                    "endDateTime": "2025-05-01T12:00:00Z",
                    "location": "Lviv",
                    "onlineLink": null,
                    "allDay": false
                  }],
                  "tags": [],
                  "online": false,
                  "initiativeTypes": ["EDUCATIONAL"]
                }
            """;

        MockMultipartFile invalidPart = new MockMultipartFile(
            "createEventDto", "dto.json", "application/json", invalidJson.getBytes()
        );

        mockMvc.perform(multipart("/events/create")
                .file(invalidPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .principal(principal))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "User", roles = "USER")
        // This will simulate an authenticated user
    void deleteEvent_success() throws Exception {
        Long eventId = 1L;

        doNothing().when(eventService).deleteById(any(), any());

        mockMvc.perform(delete("/events/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify that the service was called
        Mockito.verify(eventService).deleteById(any(), any());
    }
}
