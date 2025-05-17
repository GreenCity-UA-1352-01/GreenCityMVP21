package greencity.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.ModelUtils;
import greencity.config.SecurityConfig;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.user.UserVO;
import greencity.security.jwt.JwtTool;
import greencity.service.EventCommentService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EventCommentController.class)
@ContextConfiguration(classes = {EventCommentController.class, SecurityConfig.class})
class EventCommentControllerTest {

    private static final String CONTROLLER_LINK;
    private static final AddEventCommentDtoRequest REQUEST;
    private static final AddEventCommentDtoResponse RESPONSE;

    static {
        CONTROLLER_LINK = "/events/comments";
        REQUEST = ModelUtils.getAddEventCommentDtoRequest();
        RESPONSE = ModelUtils.getAddEventCommentDtoResponse();
    }

    @MockBean
    private EventCommentService eventCommentService;
    @MockBean
    private UserService userService;
    @MockBean
    private JwtTool jwtTool;
    @MockBean
    private ModelMapper modelMapper;
    private final ObjectMapper objectMapper = ModelUtils.getObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void testSave() throws Exception {
        String content = """
            {
              "parentCommentId": null,
              "text": "%s"
            }
            """.formatted(REQUEST.getText());

        when(eventCommentService.save(eq(1L), eq(REQUEST), any(UserVO.class))).thenReturn(RESPONSE);

        mockMvc.perform(post(CONTROLLER_LINK + "/{eventId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(RESPONSE)));

        verify(eventCommentService).save(eq(1L), eq(REQUEST), any(UserVO.class));
    }

    @Test
    void testSave_whenNotAuthenticated() throws Exception {
        mockMvc.perform(post(CONTROLLER_LINK + "/{eventId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isUnauthorized());

        verify(eventCommentService, never()).save(any(), any(), any());
    }

    @Test
    @WithMockUser
    void testSave_whenInvalidRequestBody() throws Exception {
        mockMvc.perform(post(CONTROLLER_LINK + "/{eventId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());

        verify(eventCommentService, never()).save(any(), any(), any());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void testDeleteEventComment() throws Exception {
        Long commentId = 1L;

        mockMvc.perform(delete(CONTROLLER_LINK + "/{commentId}", commentId))
            .andExpect(status().isOk());

        verify(eventCommentService).deleteComment(eq(commentId), any(UserVO.class));
    }

    @Test
    void testDeleteEventComment_whenNotAuthenticated() throws Exception {
        Long commentId = 1L;

        mockMvc.perform(delete(CONTROLLER_LINK + "/{commentId}", commentId))
            .andExpect(status().isUnauthorized());

        verify(eventCommentService, never()).deleteComment(any(), any());
    }
}
