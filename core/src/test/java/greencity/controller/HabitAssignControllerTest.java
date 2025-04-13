package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.habit.*;
import greencity.dto.user.UserVO;
import greencity.enums.HabitAssignStatus;
import greencity.exception.exceptions.UserShoppingListItemNotSavedException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitAssignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static greencity.ModelUtils.getPrincipal;
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

    private static final String habitAssignLink = "/habit/assign";

    private MockMvc mockMvc;

    @Mock
    private HabitAssignService habitAssignService;

    @InjectMocks
    private HabitAssignController habitAssignController;

    @Mock
    private ObjectMapper objectMapper;

    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    private Principal principal = getPrincipal();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(habitAssignController)
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();
    }

    @Test
    void assignDefaultTest() throws Exception {
        Long habitId = 1L;
        UserVO user = new UserVO();
        HabitAssignManagementDto dto = new HabitAssignManagementDto();

        when(habitAssignService.assignDefaultHabitForUser(habitId, user)).thenReturn(dto);

        mockMvc.perform(post(habitAssignLink + "/{habitId}", habitId)
                        .principal(principal)
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

        String mockJson = "{\"some\":\"value\"}";

        when(habitAssignService.assignCustomHabitForUser(habitId, user, requestDto)).thenReturn(responseList);

        mockMvc.perform(post(habitAssignLink + "/{habitId}/custom", habitId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mockJson)
                .principal(principal))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).assignCustomHabitForUser(habitId, user, requestDto);
    }

    @Test
    void assignCustomTest_InvalidJson() throws Exception {
        Long habitId = 1L;
        String invalidJson = "{some:value}";

        mockMvc.perform(post(habitAssignLink + "/{habitId}/custom", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
                        .principal(principal))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateHabitAssignDurationTest() throws Exception {
        Long habitId = 1L;
        UserVO user = new UserVO();
        Integer duration = 5;
        HabitAssignUserDurationDto responseDto = new HabitAssignUserDurationDto();

        when(habitAssignService.updateUserHabitInfoDuration(habitId, user.getId(), duration)).thenReturn(responseDto);

        mockMvc.perform(put(habitAssignLink + "/{habitAssignId}/update-habit-duration", habitId)
                .principal(principal)
                .param("duration", String.valueOf(duration))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).updateUserHabitInfoDuration(habitId, user.getId(), duration);
    }


    @Test
    void updateHabitAssignDurationTest_MissingParam() throws Exception {
        Long habitAssignId = 1L;

        mockMvc.perform(put(habitAssignLink + "/{habitAssignId}/update-habit-duration", habitAssignId)
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getHabitAssignTest() throws Exception {
        Long habitAssignId = 1L;
        UserVO user = new UserVO();
        Locale locale = Locale.ENGLISH;
        HabitAssignDto responseDto = new HabitAssignDto();

        when(habitAssignService.getByHabitAssignIdAndUserId(habitAssignId, user.getId(), locale.getLanguage()))
                .thenReturn(responseDto);

        mockMvc.perform(get(habitAssignLink + "/{habitAssignId}", habitAssignId)
                .principal(principal)
                .header("Accept-Language", "en")
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

        mockMvc.perform(get(habitAssignLink + "/allForCurrentUser")
                        .principal(principal)
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

        mockMvc.perform(get(habitAssignLink + "/{habitAssignId}/allUserAndCustomList", habitAssignId)
                        .principal(principal)
                        .header("Accept-Language", locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).getUserShoppingAndCustomShoppingLists(user.getId(), habitAssignId, locale.getLanguage());
    }

    @Test
    void getUserShoppingAndCustomShoppingLists_NotSavedTest() throws Exception {
        Long habitAssignId = 1L;
        UserVO user = new UserVO();
        Locale locale = Locale.ENGLISH;

        when(habitAssignService.getUserShoppingAndCustomShoppingLists(user.getId(), habitAssignId, locale.getLanguage()))
                .thenThrow(new UserShoppingListItemNotSavedException("Shopping list items not saved"));

        mockMvc.perform(get(habitAssignLink + "/{habitAssignId}/allUserAndCustomList", habitAssignId)
                        .principal(principal)
                        .header("Accept-Language", locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).getUserShoppingAndCustomShoppingLists(user.getId(), habitAssignId, locale.getLanguage());
    }

    @Test
    void updateUserAndCustomShoppingListsTest() throws Exception {
        Long habitAssignId = 1L;
        UserVO user = new UserVO();
        Locale locale = Locale.ENGLISH;
        UserShoppingAndCustomShoppingListsDto listsDto = new UserShoppingAndCustomShoppingListsDto();

        String mockJson = "{\"some\":\"value\"}";

        doNothing().when(habitAssignService)
                .fullUpdateUserAndCustomShoppingLists(user.getId(), habitAssignId, listsDto, locale.getLanguage());

        mockMvc.perform(put(habitAssignLink + "/{habitAssignId}/allUserAndCustomList", habitAssignId)
                        .principal(principal)
                        .header("Accept-Language", locale.getLanguage())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mockJson))
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

        mockMvc.perform(get(habitAssignLink + "/allUserAndCustomShoppingListsInprogress")
                        .principal(principal)
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

        mockMvc.perform(get(habitAssignLink + "/{habitId}/all", habitId)
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

        mockMvc.perform(get(habitAssignLink + "/{habitId}/active", habitId)
                        .principal(principal)
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

        mockMvc.perform(get(habitAssignLink + "/{habitAssignId}/more", habitAssignId)
                .principal(principal)
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

        String mockJson = "{\"status\":\"INPROGRESS\"}";

        when(habitAssignService.updateStatusByHabitAssignId(habitAssignId, requestDto)).thenReturn(responseDto);

        mockMvc.perform(patch(habitAssignLink + "/{habitAssignId}", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mockJson))
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

        mockMvc.perform(post(habitAssignLink + "/{habitAssignId}/enroll/{date}", habitAssignId, date)
                        .principal(principal)
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

        mockMvc.perform(post(habitAssignLink + "/{habitAssignId}/unenroll/{date}", habitAssignId, date)
                        .principal(principal)
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

        mockMvc.perform(get(habitAssignLink + "/active/{date}", date)
                        .principal(principal)
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

        mockMvc.perform(get(habitAssignLink + "/activity/{from}/to/{to}", from, to)
                        .principal(principal)
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

        mockMvc.perform(patch(habitAssignLink + "/cancel/{habitId}", habitId)
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(habitAssignService).cancelHabitAssign(habitId, user.getId());
    }

    @Test
    void deleteHabitAssignTest() throws Exception {
        Long habitAssignId = 1L;
        UserVO user = new UserVO();

        mockMvc.perform(delete(habitAssignLink + "/delete/{habitAssignId}", habitAssignId)
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).deleteHabitAssign(habitAssignId, user.getId());
    }

    @Test
    void updateShoppingListStatusTest() throws Exception {
        UpdateUserShoppingListDto requestDto = new UpdateUserShoppingListDto();
        String mockJson = "{\"some\":\"value\"}";

        doNothing().when(habitAssignService).updateUserShoppingListItem(requestDto);

        mockMvc.perform(put(habitAssignLink + "/saveShoppingListForHabitAssign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mockJson))
                .andExpect(status().isOk());

        verify(habitAssignService).updateUserShoppingListItem(requestDto);

    }

    @Test
    void updateProgressNotificationHasDisplayedTest() throws Exception {
        Long habitAssignId = 1L;
        UserVO user = new UserVO();

        doNothing().when(habitAssignService).updateProgressNotificationHasDisplayed(habitAssignId, user.getId());

        mockMvc.perform(put(habitAssignLink + "/{habitAssignId}/updateProgressNotificationHasDisplayed", habitAssignId)
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).updateProgressNotificationHasDisplayed(habitAssignId, user.getId());
    }
}
