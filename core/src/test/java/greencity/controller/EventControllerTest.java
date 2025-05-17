package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.ModelUtils;
import greencity.config.SecurityConfig;
import greencity.converters.UserArgumentResolver;
import greencity.dto.event.CreateEventDtoResponse;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.event.UpdateEventDtoRequest;
import greencity.dto.event.UpdateEventDtoResponse;
import greencity.dto.user.UserVO;
import greencity.service.EventService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.List;

import static greencity.ModelUtils.getUserVO;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private EventController eventController;

    private final Principal principal = () -> "test@gmail.com";

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

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
                          "startDateTime": "%s",
                          "endDateTime": "%s",
                          "location": "Lviv",
                          "onlineLink": null,
                          "allDay": false
                        }
                      ],
                      "tags": ["Еко"],
                      "online": false,
                      "initiativeTypes": ["EDUCATIONAL"]
                    }
                """.formatted(
                ZonedDateTime.now().plusDays(1).toString(),
                ZonedDateTime.now().plusDays(1).plusHours(1).toString());

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
                                ZonedDateTime.now().plusDays(1),
                                ZonedDateTime.now().plusDays(1).plusHours(1),
                                "Lviv",
                                null
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

    @Test
    void updateEvent_Success() throws Exception {
        UserVO userVO = getUserVO();
        UpdateEventDtoRequest request = ModelUtils.getUpdateEventDtoRequest();
        UpdateEventDtoResponse response = ModelUtils.getUpdateEventDtoResponse();

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(userVO, UserVO.class)).thenReturn(userVO);
        when(eventService.updateEvent(any(), any(), eq(userVO))).thenReturn(response);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "updateEventDtoRequest", "update.json", "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile image = new MockMultipartFile(
                "images", "main.jpg", MediaType.IMAGE_JPEG_VALUE, "dummy".getBytes()
        );

        mockMvc.perform(multipart("/events/update")
                        .file(jsonPart)
                        .file(image)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.description").value("description with more than 20 characters"))
                .andExpect(jsonPath("$.dateTimes[0].location").value("location"))
                .andExpect(jsonPath("$.mainImage.imagePath").value("main.jpg"))
                .andExpect(jsonPath("$.eventImages[0].imagePath").value("https://cdn.com/file/main.jpg"))
                .andExpect(jsonPath("$.eventImages[1].imagePath").value("https://cdn.com/file/second.jpg"))
                .andExpect(jsonPath("$.tags[0].tagTranslations[0].name").value("Новини"))
                .andExpect(jsonPath("$.open").value(true));
        verify(eventService).updateEvent(any(), any(), eq(userVO));
    }

    @Test
    void updateEvent_ShouldReturnBadRequest_WhenDtoInvalid() throws Exception {
        UpdateEventDtoRequest invalidReq = ModelUtils.getUpdateEventDtoRequest();
        invalidReq.setDescription("short");

        MockMultipartFile jsonPart = new MockMultipartFile(
                "updateEventDtoRequest", "update.json", "application/json",
                objectMapper.writeValueAsBytes(invalidReq)
        );

        mockMvc.perform(multipart("/events/update")
                        .file(jsonPart)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(userService, eventService, modelMapper);
    }

    @Test
    void likeEvent_Success() throws Exception {
        Long eventId = 1L;
        UserVO userVO = getUserVO();

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        doNothing().when(eventService).likeEvent(eq(eventId), any(UserVO.class));

        mockMvc.perform(post("/events/{id}/like", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(eventService).likeEvent(eq(eventId), any(UserVO.class));
    }

    @Test
    void unlikeEvent_Success() throws Exception {
        Long eventId = 1L;
        UserVO userVO = getUserVO();

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        doNothing().when(eventService).unlikeEvent(eq(eventId), any(UserVO.class));

        mockMvc.perform(delete("/events/{id}/like", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(eventService).unlikeEvent(eq(eventId), any(UserVO.class));
    }
}
