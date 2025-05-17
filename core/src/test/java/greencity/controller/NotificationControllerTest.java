package greencity.controller;

import greencity.controller.NotificationController;
import greencity.dto.notification.NotificationResponseDto;
import greencity.enums.NotificationStatus;
import greencity.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

@WebMvcTest(NotificationController.class)
@ContextConfiguration (classes = NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @WithMockUser(username = "user12@gmail.com")
    @Test
    void testGetUserNotifications_returnsListOfNotifications() throws Exception {
        Long userId = 1L;
        NotificationResponseDto dto = NotificationResponseDto.builder()
                .id(1L)
                .action("liked")
                .objectName("Some News")
                .creationDate(ZonedDateTime.now())
                .status(NotificationStatus.UNREAD)
                .receiverId(userId)
                .initiatorId(2L)
                .build();

        when(notificationService.getAllNotificationsForUser(userId)).thenReturn(List.of(dto));

        mockMvc.perform(get("/notifications/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].action", is("liked")))
                .andExpect(jsonPath("$[0].objectName", is("Some News")));
    }
}
