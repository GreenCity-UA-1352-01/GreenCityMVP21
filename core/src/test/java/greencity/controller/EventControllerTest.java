package greencity.controller;

import greencity.GreenCityApplication;
import greencity.config.SecurityConfig;
import greencity.dto.user.UserVO;
import greencity.security.jwt.JwtTool;
import greencity.service.EventService;
import greencity.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@ContextConfiguration(classes = {GreenCityApplication.class, SecurityConfig.class})
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private UserService userService;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private JwtTool jwtTool;


    @Test
    @WithMockUser
            (username = "User", roles = "USER")
        // This will simulate an authenticated user
    void deleteEvent_success() throws Exception {
        Long eventId = 1L;

        UserVO mockUser = UserVO.builder()
                .id(100L)
                .role(greencity.enums.Role.ROLE_USER) // Or ROLE_ADMIN
                .build();
        doNothing().when(eventService).deleteById(any(), any());

        mockMvc.perform(delete("/event/{id}", eventId)
                        .principal(() -> "mockUser") // Simulate principal if needed
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify that the service was called
        Mockito.verify(eventService).deleteById(any(), any());
    }
}
