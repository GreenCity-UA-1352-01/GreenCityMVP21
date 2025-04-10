package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.habit.*;
import greencity.dto.user.UserVO;
import greencity.enums.HabitAssignStatus;
import greencity.service.HabitAssignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HabitAssignControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HabitAssignService habitAssignService;

    @InjectMocks
    private HabitAssignController habitAssignController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(habitAssignController).build();
    }

    @Test
    void assignDefaultTest() throws Exception {
        Long habitId = 1L;
        UserVO user = new UserVO();
        HabitAssignManagementDto dto = new HabitAssignManagementDto();

        when(habitAssignService.assignDefaultHabitForUser(habitId, user)).thenReturn(dto);

        mockMvc.perform(post("/habit/assign/{habitId}", habitId)
                        .requestAttr("user", user)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).assignDefaultHabitForUser(habitId, user);
    }

    @Test
    void assignCustomTest() throws Exception {
        Long habitId = 1L;
        UserVO user = new UserVO();
        HabitAssignCustomPropertiesDto requestDto = new HabitAssignCustomPropertiesDto();
        List<HabitAssignManagementDto> responseList = List.of(new HabitAssignManagementDto());

        when(habitAssignService.assignCustomHabitForUser(habitId, user, requestDto)).thenReturn(responseList);

        mockMvc.perform(post("/habit/assign/{habitId}/custom", habitId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .requestAttr("user", user))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).assignCustomHabitForUser(habitId, user, requestDto);
    }

    @Test
    void updateHabitAssignDurationTest() throws Exception {
        Long habitId = 1L;
        UserVO user = new UserVO();
        Integer duration = 5;
        HabitAssignUserDurationDto responseDto = new HabitAssignUserDurationDto();

        when(habitAssignService.updateUserHabitInfoDuration(habitId, user.getId(), duration)).thenReturn(responseDto);

        mockMvc.perform(put("/habit/assign/{habitAssignId}/update-habit-duration", habitId)
                .requestAttr("user", user)
                .param("duration", String.valueOf(duration))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).updateUserHabitInfoDuration(habitId, user.getId(), duration);
    }

    @Test
    void getHabitAssignTest() throws Exception {
        Long habitAssignId = 1L;
        UserVO user = new UserVO();
        Locale locale = Locale.ENGLISH;
        HabitAssignDto responseDto = new HabitAssignDto();

        when(habitAssignService.getByHabitAssignIdAndUserId(habitAssignId, user.getId(), locale.getLanguage()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/habit/assign/{habitAssignId}", habitAssignId)
                .requestAttr("user", user)
                .header("Accept-Language", locale.getLanguage())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).getByHabitAssignIdAndUserId(habitAssignId, user.getId(), locale.getLanguage());
    }

    @Test
    void getCurrentUserHabitAssignsByIdAndAcquiredTest() throws Exception {
        UserVO user = new UserVO();
        Locale locale = Locale.ENGLISH;
        List<HabitAssignDto> responseDto = List.of(new HabitAssignDto());

        when(habitAssignService.getAllHabitAssignsByUserIdAndStatusNotCancelled(user.getId(), locale.getLanguage()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/habit/assign/allForCurrentUser")
                        .requestAttr("user", user)
                        .header("Accept-Language", locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).getAllHabitAssignsByUserIdAndStatusNotCancelled(user.getId(), locale.getLanguage());
    }

    @Test
    void getUserShoppingAndCustomShoppingListsTest() throws Exception {
        Long habitAssignId = 1L;
        UserVO user = new UserVO();
        Locale locale = Locale.ENGLISH;
        UserShoppingAndCustomShoppingListsDto responseDto = new UserShoppingAndCustomShoppingListsDto();

        when(habitAssignService.getUserShoppingAndCustomShoppingLists(user.getId(), habitAssignId, locale.getLanguage()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/habit/assign/{habitAssignId}/allUserAndCustomList", habitAssignId)
                        .requestAttr("user", user)
                        .header("Accept-Language", locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).getUserShoppingAndCustomShoppingLists(user.getId(), habitAssignId, locale.getLanguage());
    }

    @Test
    void updateUserAndCustomShoppingListsTest() throws Exception {
        Long habitAssignId = 1L;
        UserVO user = new UserVO();
        Locale locale = Locale.ENGLISH;
        UserShoppingAndCustomShoppingListsDto listsDto = new UserShoppingAndCustomShoppingListsDto();

        doNothing().when(habitAssignService)
                .fullUpdateUserAndCustomShoppingLists(user.getId(), habitAssignId, listsDto, locale.getLanguage());

        mockMvc.perform(put("/habit/assign/{habitAssignId}/allUserAndCustomList", habitAssignId)
                        .requestAttr("user", user)
                        .header("Accept-Language", locale.getLanguage())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(listsDto)))
                .andExpect(status().isOk());

        verify(habitAssignService).fullUpdateUserAndCustomShoppingLists(user.getId(), habitAssignId, listsDto, locale.getLanguage());
    }

    @Test
    void getListOfUserAndCustomShoppingListsInprogressTest() throws Exception {
        UserVO user = new UserVO();
        Locale locale = Locale.ENGLISH;
        List<UserShoppingAndCustomShoppingListsDto> responseDto = List.of(new UserShoppingAndCustomShoppingListsDto());

        when(habitAssignService.getListOfUserAndCustomShoppingListsWithStatusInprogress(user.getId(), locale.getLanguage()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/habit/assign/allUserAndCustomShoppingListsInprogress")
                        .requestAttr("user", user)
                        .header("Accept-Language", locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).getListOfUserAndCustomShoppingListsWithStatusInprogress(user.getId(), locale.getLanguage());
    }

    @Test
    void getAllHabitAssignsByHabitIdAndAcquiredTest() throws Exception {
        Long habitId = 1L;
        Locale locale = Locale.ENGLISH;
        List<HabitAssignDto> responseDto = List.of(new HabitAssignDto());

        when(habitAssignService.getAllHabitAssignsByHabitIdAndStatusNotCancelled(habitId, locale.getLanguage()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/habit/assign/{habitId}/all", habitId)
                        .header("Accept-Language", locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).getAllHabitAssignsByHabitIdAndStatusNotCancelled(habitId, locale.getLanguage());

    }

    @Test
    void getHabitAssignByHabitIdTest() throws Exception {
        UserVO user = new UserVO();
        Long habitId = 1L;
        Locale locale = Locale.ENGLISH;
        HabitAssignDto responseDto = new HabitAssignDto();

        when(habitAssignService.findHabitAssignByUserIdAndHabitId(user.getId(), habitId, locale.getLanguage()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/habit/assign/{habitId}/active", habitId)
                        .requestAttr("user", user)
                        .header("Accept-Language", locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).findHabitAssignByUserIdAndHabitId(user.getId(), habitId, locale.getLanguage());
    }

    @Test
    void getUsersHabitByHabitAssignIdTest() throws Exception {
        UserVO user = new UserVO();
        Long habitAssignId = 1L;
        Locale locale = Locale.ENGLISH;
        HabitDto responseDto = new HabitDto();

        when(habitAssignService.findHabitByUserIdAndHabitAssignId(user.getId(), habitAssignId, locale.getLanguage()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/habit/assign/{habitAssignId}/more", habitAssignId)
                .requestAttr("user", user)
                .header("Accept-Language", locale.getLanguage())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).findHabitByUserIdAndHabitAssignId(user.getId(), habitAssignId, locale.getLanguage());
    }

    @Test
    void updateAssignByHabitIdTest() throws Exception {
        Long habitAssignId = 1L;
        HabitAssignStatDto requestDto = new HabitAssignStatDto();
        HabitAssignManagementDto responseDto = new HabitAssignManagementDto();
        requestDto.setStatus(HabitAssignStatus.INPROGRESS);

        when(habitAssignService.updateStatusByHabitAssignId(habitAssignId, requestDto)).thenReturn(responseDto);

        mockMvc.perform(patch("/habit/assign/{habitAssignId}", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void enrollHabitTest() throws Exception {
        Long habitAssignId = 1L;
        UserVO user = new UserVO();
        LocalDate date = LocalDate.of(2025, 4, 10);
        Locale locale = Locale.ENGLISH;
        HabitAssignDto responseDto = new HabitAssignDto();

        when(habitAssignService.enrollHabit(habitAssignId, user.getId(), date, locale.getLanguage()))
                .thenReturn(responseDto);

        mockMvc.perform(post("/habit/assign/{habitAssignId}/enroll/{date}", habitAssignId, date)
                        .requestAttr("user", user)
                        .header("Accept-Language", locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).enrollHabit(habitAssignId, user.getId(), date, locale.getLanguage());
    }

    @Test
    void unenrollHabitTest() throws Exception {
        Long habitAssignId = 1L;
        UserVO user = new UserVO();
        LocalDate date = LocalDate.of(2025, 4, 10);
        HabitAssignDto responseDto = new HabitAssignDto();

        when(habitAssignService.unenrollHabit(habitAssignId, user.getId(), date))
                .thenReturn(responseDto);

        mockMvc.perform(post("/habit/assign/{habitAssignId}/unenroll/{date}", habitAssignId, date)
                        .requestAttr("user", user)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).unenrollHabit(habitAssignId, user.getId(), date);
    }

    @Test
    void getInprogressHabitAssignOnDateTest() throws Exception {
        UserVO user = new UserVO();
        LocalDate date = LocalDate.of(2025, 4, 10);
        Locale locale = Locale.ENGLISH;
        List<HabitAssignDto> responseDto = List.of(new HabitAssignDto());

        when(habitAssignService.findInprogressHabitAssignsOnDate(user.getId(), date, locale.getLanguage()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/habit/assign/active/{date}", date)
                        .requestAttr("user", user)
                        .header("Accept-Language", locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).findInprogressHabitAssignsOnDate(user.getId(), date, locale.getLanguage());
    }

    @Test
    void getHabitAssignBetweenDatesTest() throws Exception {
        UserVO user = new UserVO();
        LocalDate from = LocalDate.of(2025, 4, 1);
        LocalDate to = LocalDate.of(2025, 4, 10);
        Locale locale = Locale.ENGLISH;
        List<HabitsDateEnrollmentDto> responseDto = List.of(new HabitsDateEnrollmentDto());

        when(habitAssignService.findHabitAssignsBetweenDates(user.getId(), from, to, locale.getLanguage()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/habit/assign/activity/{from}/to/{to}", from, to)
                        .requestAttr("user", user)
                        .header("Accept-Language", locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).findHabitAssignsBetweenDates(user.getId(), from, to, locale.getLanguage());
    }

    @Test
    void cancelHabitAssignTest() throws Exception {
        Long habitId = 1L;
        UserVO user = new UserVO();
        HabitAssignDto responseDto = new HabitAssignDto();

        when(habitAssignService.cancelHabitAssign(habitId, user.getId())).thenReturn(responseDto);

        mockMvc.perform(patch("/habit/assign/cancel/{habitId}", habitId)
                        .requestAttr("user", user)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).cancelHabitAssign(habitId, user.getId());
    }

    @Test
    void deleteHabitAssignTest() throws Exception {
        Long habitAssignId = 1L;
        UserVO user = new UserVO();

        mockMvc.perform(delete("/habit/assign/delete/{habitAssignId}", habitAssignId)
                        .requestAttr("user", user)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).deleteHabitAssign(habitAssignId, user.getId());
    }

    @Test
    void updateShoppingListStatusTest() throws Exception {
        UpdateUserShoppingListDto requestDto = new UpdateUserShoppingListDto();

        doNothing().when(habitAssignService).updateUserShoppingListItem(requestDto);

        mockMvc.perform(put("/habit/assign/saveShoppingListForHabitAssign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(habitAssignService).updateUserShoppingListItem(requestDto);

    }

    @Test
    void updateProgressNotificationHasDisplayedTest() throws Exception {
        Long habitAssignId = 1L;
        UserVO user = new UserVO();

        doNothing().when(habitAssignService).updateProgressNotificationHasDisplayed(habitAssignId, user.getId());

        mockMvc.perform(put("/habit/assign/{habitAssignId}/updateProgressNotificationHasDisplayed", habitAssignId)
                        .requestAttr("user", user)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).updateProgressNotificationHasDisplayed(habitAssignId, user.getId());
    }
}
