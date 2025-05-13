package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.GreenCityApplication;
import greencity.config.SecurityConfig;
import greencity.dto.notification.UpdateNotificationStatusRequestDto;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.security.jwt.JwtTool;
import greencity.service.LanguageService;
import greencity.service.NotificationService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@ContextConfiguration(classes = {GreenCityApplication.class, SecurityConfig.class})
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private UserService userService;

    @MockBean
    private LanguageService languageService;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private JwtTool jwtTool;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithMockUser
    public void updateNotificationStatus_Correct_StatusChanged() throws Exception {

        var updateNotificationStatusRequestDto =
                UpdateNotificationStatusRequestDto
                        .builder()
                        .id(1L)
                        .status("READ")
                        .build();

        String email = "user@example.com";

        UserVO userVO = new UserVO();
        userVO.setId(123L);

        when(userService.findByEmail(email)).thenReturn(userVO);

        mockMvc.perform(put("/notifications/status")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateNotificationStatusRequestDto))
                )
                .andExpect(status().isOk());

        verify(notificationService, times(1)).updateNotificationStatus(any(UpdateNotificationStatusRequestDto.class), any());
    }

    @Test
    public void updateNotificationStatus_Unauthorized_UnauthorizedExceptionThrown() throws Exception {

        var updateNotificationStatusRequestDto =
                UpdateNotificationStatusRequestDto
                        .builder()
                        .id(1L)
                        .status("READ")
                        .build();

        mockMvc.perform(put("/notifications/status")
                        .content(objectMapper.writeValueAsString(updateNotificationStatusRequestDto)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(notificationService);
    }

    @Test
    @WithMockUser
    public void updateNotificationStatus_NotificationNotBelongToUser_BadRequestExceptionThrown() throws Exception {

        var updateNotificationStatusRequestDto =
                UpdateNotificationStatusRequestDto
                        .builder()
                        .id(1L)
                        .status("READ")
                        .build();

        doThrow(new BadRequestException("Notification with ID " + updateNotificationStatusRequestDto.getId() + " does not belong to user with ID " + 1L + "."))
                .when(notificationService)
                .updateNotificationStatus(any(), any());

        mockMvc.perform(put("/notifications/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateNotificationStatusRequestDto)))
                .andExpect(status().isBadRequest());

        verify(notificationService, times(1)).updateNotificationStatus(any(UpdateNotificationStatusRequestDto.class), any());
    }

    @Test
    @WithMockUser
    public void updateNotificationStatus_WrongStatus_BadRequestExceptionThrown() throws Exception {

        var updateNotificationStatusRequestDto =
                UpdateNotificationStatusRequestDto
                        .builder()
                        .id(1L)
                        .status("WRONG")
                        .build();

        doThrow(new BadRequestException(""))
                .when(notificationService)
                .updateNotificationStatus(any(), any());

        mockMvc.perform(put("/notifications/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateNotificationStatusRequestDto)))
                .andExpect(status().isBadRequest());

        verify(notificationService, times(1)).updateNotificationStatus(any(UpdateNotificationStatusRequestDto.class), any());
    }

    @Test
    @WithMockUser
    public void updateNotificationStatus_NotificationNotExists_NotFoundExceptionThrown() throws Exception {

        var updateNotificationStatusRequestDto =
                UpdateNotificationStatusRequestDto
                        .builder()
                        .id(1L)
                        .status("WRONG")
                        .build();

        doThrow(new NotFoundException("Notification with ID " + updateNotificationStatusRequestDto.getId() + " not found."))
                .when(notificationService)
                .updateNotificationStatus(any(), any());

        mockMvc.perform(put("/notifications/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateNotificationStatusRequestDto)))
                .andExpect(status().isNotFound());

        verify(notificationService, times(1)).updateNotificationStatus(any(UpdateNotificationStatusRequestDto.class), any());
    }
}
