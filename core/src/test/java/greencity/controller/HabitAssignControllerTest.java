package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.habit.*;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.user.UserShoppingListItemAdvanceDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.dto.user.UserVO;
import greencity.enums.HabitAssignStatus;
import greencity.enums.ShoppingListItemStatus;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;

import static greencity.ModelUtils.getPrincipal;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.status").value(dto.getStatus()))
                .andExpect(jsonPath("$.habitId").value(dto.getHabitId()));

        verify(habitAssignService).assignDefaultHabitForUser(habitId, user);
    }

    @Test
    void assignCustomTest() throws Exception {
        Long habitId = 1L;
        UserVO user = new UserVO();
        HabitAssignCustomPropertiesDto requestDto = new HabitAssignCustomPropertiesDto();

        HabitAssignManagementDto dto = HabitAssignManagementDto.builder()
                .id(1L)
                .status(HabitAssignStatus.INPROGRESS)
                .createDateTime(ZonedDateTime.now())
                .habitId(100L)
                .userId(200L)
                .duration(30)
                .workingDays(5)
                .habitStreak(10)
                .lastEnrollment(ZonedDateTime.now())
                .progressNotificationHasDisplayed(false)
                .build();

        List<HabitAssignManagementDto> responseList = List.of(dto);

        when(habitAssignService.assignCustomHabitForUser(habitId, user, requestDto)).thenReturn(responseList);

        mockMvc.perform(post(habitAssignLink + "/{habitId}/custom", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"some\":\"value\"}")
                        .principal(principal))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("INPROGRESS"))
                .andExpect(jsonPath("$[0].habitId").value(100L))
                .andExpect(jsonPath("$[0].userId").value(200L));

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

        HabitAssignUserDurationDto responseDto = HabitAssignUserDurationDto.builder()
                .habitAssignId(1L)
                .userId(user.getId())
                .habitId(100L)
                .status(HabitAssignStatus.INPROGRESS)
                .workingDays(5)
                .duration(duration)
                .build();

        when(habitAssignService.updateUserHabitInfoDuration(habitId, user.getId(), duration)).thenReturn(responseDto);

        mockMvc.perform(put(habitAssignLink + "/{habitAssignId}/update-habit-duration", habitId)
                .principal(principal)
                .param("duration", String.valueOf(duration))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.habitAssignId").value(1L))
                .andExpect(jsonPath("$.habitId").value(100L))
                .andExpect(jsonPath("$.status").value("INPROGRESS"))
                .andExpect(jsonPath("$.duration").value(duration));

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
        responseDto.setId(habitAssignId);
        responseDto.setStatus(HabitAssignStatus.INPROGRESS);

        when(habitAssignService.getByHabitAssignIdAndUserId(habitAssignId, user.getId(), locale.getLanguage()))
                .thenReturn(responseDto);

        mockMvc.perform(get(habitAssignLink + "/{habitAssignId}", habitAssignId)
                        .principal(principal)
                        .header("Accept-Language", "en")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(habitAssignId))
                .andExpect(jsonPath("$.status").value(HabitAssignStatus.INPROGRESS.toString()));

        verify(habitAssignService).getByHabitAssignIdAndUserId(habitAssignId, user.getId(), locale.getLanguage());
    }

    @Test
    void getCurrentUserHabitAssignsByIdAndAcquiredTest() throws Exception {
        UserVO user = new UserVO();
        Locale locale = Locale.ENGLISH;

        List<HabitAssignDto> responseDto = List.of(HabitAssignDto.builder()
                .id(1L)
                .status(HabitAssignStatus.INPROGRESS)
                .duration(10)
                .build());

        when(habitAssignService.getAllHabitAssignsByUserIdAndStatusNotCancelled(user.getId(), locale.getLanguage()))
                .thenReturn(responseDto);

        mockMvc.perform(get(habitAssignLink + "/allForCurrentUser")
                        .principal(principal)
                        .header("Accept-Language", locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("INPROGRESS"))
                .andExpect(jsonPath("$[0].duration").value(10));

        verify(habitAssignService).getAllHabitAssignsByUserIdAndStatusNotCancelled(user.getId(), locale.getLanguage());
    }

    @Test
    void getUserShoppingAndCustomShoppingListsTest() throws Exception {
        Long habitAssignId = 1L;
        UserVO user = new UserVO();
        Locale locale = Locale.ENGLISH;

        UserShoppingAndCustomShoppingListsDto responseDto = new UserShoppingAndCustomShoppingListsDto(
                List.of(new UserShoppingListItemResponseDto(1L, "Item 1", ShoppingListItemStatus.ACTIVE)),
                List.of(new CustomShoppingListItemResponseDto(2L, "Item 1", ShoppingListItemStatus.DONE))
        );

        when(habitAssignService.getUserShoppingAndCustomShoppingLists(user.getId(), habitAssignId, locale.getLanguage()))
                .thenReturn(responseDto);

        mockMvc.perform(get(habitAssignLink + "/{habitAssignId}/allUserAndCustomList", habitAssignId)
                        .principal(principal)
                        .header("Accept-Language", locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userShoppingListItemDto[0].id").value(1L))
                .andExpect(jsonPath("$.userShoppingListItemDto[0].text").value("Item 1"))
                .andExpect(jsonPath("$.userShoppingListItemDto[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.customShoppingListItemDto[0].id").value(2L))
                .andExpect(jsonPath("$.customShoppingListItemDto[0].text").value("Item 1"));

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

        UserShoppingAndCustomShoppingListsDto responseDto = UserShoppingAndCustomShoppingListsDto.builder()
                .userShoppingListItemDto(List.of(new UserShoppingListItemResponseDto(1L, "Item 1", ShoppingListItemStatus.INPROGRESS)))
                .customShoppingListItemDto(List.of(new CustomShoppingListItemResponseDto(2L, "Item 1", ShoppingListItemStatus.INPROGRESS)))
                .build();

        when(habitAssignService.getListOfUserAndCustomShoppingListsWithStatusInprogress(user.getId(), locale.getLanguage()))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get(habitAssignLink + "/allUserAndCustomShoppingListsInprogress")
                        .principal(principal)
                        .header("Accept-Language", locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].userShoppingListItemDto[0].id").value(1L))
                .andExpect(jsonPath("$[0].customShoppingListItemDto[0].id").value(2L));

        verify(habitAssignService).getListOfUserAndCustomShoppingListsWithStatusInprogress(user.getId(), locale.getLanguage());
    }

    @Test
    void getAllHabitAssignsByHabitIdAndAcquiredTest() throws Exception {
        Long habitId = 1L;
        Locale locale = Locale.ENGLISH;
        HabitAssignDto responseDto = HabitAssignDto.builder()
                .id(1L)
                .status(HabitAssignStatus.INPROGRESS)
                .build();

        when(habitAssignService.getAllHabitAssignsByHabitIdAndStatusNotCancelled(habitId, locale.getLanguage()))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get(habitAssignLink + "/{habitId}/all", habitId)
                        .header("Accept-Language", locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("INPROGRESS"));

        verify(habitAssignService).getAllHabitAssignsByHabitIdAndStatusNotCancelled(habitId, locale.getLanguage());
    }

    @Test
    void getHabitAssignByHabitIdTest() throws Exception {
        UserVO user = new UserVO();
        Long habitId = 1L;
        Locale locale = Locale.ENGLISH;
        HabitAssignDto responseDto = HabitAssignDto.builder()
                .id(1L)
                .status(HabitAssignStatus.INPROGRESS)
                .build();

        when(habitAssignService.findHabitAssignByUserIdAndHabitId(user.getId(), habitId, locale.getLanguage()))
                .thenReturn(responseDto);

        mockMvc.perform(get(habitAssignLink + "/{habitId}/active", habitId)
                        .principal(principal)
                        .header("Accept-Language", locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("INPROGRESS"));

        verify(habitAssignService).findHabitAssignByUserIdAndHabitId(user.getId(), habitId, locale.getLanguage());
    }

    @Test
    void getUsersHabitByHabitAssignIdTest() throws Exception {
        UserVO user = new UserVO();
        Long habitAssignId = 1L;
        Locale locale = Locale.ENGLISH;

        HabitDto responseDto = HabitDto.builder()
                .id(1L)
                .isCustomHabit(true)
                .build();

        when(habitAssignService.findHabitByUserIdAndHabitAssignId(user.getId(), habitAssignId, locale.getLanguage()))
                .thenReturn(responseDto);

        mockMvc.perform(get(habitAssignLink + "/{habitAssignId}/more", habitAssignId)
                .principal(principal)
                .header("Accept-Language", locale.getLanguage())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.isCustomHabit").value(true));

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
    @WithMockUser
    void updateAssignByHabitId_InvalidContent_BadRequestExceptionThrown() throws Exception {
        Long habitAssignId = 1L;

        String invalidJson = "{}";

        mockMvc.perform(patch(habitAssignLink + "/{habitAssignId}", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists());

        verifyNoInteractions(habitAssignService);
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

        HabitAssignDto responseDto = HabitAssignDto.builder()
                .id(1L)
                .status(HabitAssignStatus.INPROGRESS)
                .build();

        List<HabitAssignDto> responseDtoList = List.of(responseDto);

        when(habitAssignService.findInprogressHabitAssignsOnDate(user.getId(), date, locale.getLanguage()))
                .thenReturn(responseDtoList);

        mockMvc.perform(get(habitAssignLink + "/active/{date}", date)
                        .principal(principal)
                        .header("Accept-Language", locale.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("INPROGRESS"));

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
        UserShoppingListItemAdvanceDto itemAdvanceDto = UserShoppingListItemAdvanceDto.builder()
                .id(1L)
                .shoppingListItemId(10L)
                .status(ShoppingListItemStatus.INPROGRESS)
                .content("Test content")
                .build();

        UpdateUserShoppingListDto updateDto = UpdateUserShoppingListDto.builder()
                .habitAssignId(100L)
                .userShoppingListItemId(200L)
                .userShoppingListAdvanceDto(List.of(itemAdvanceDto))
                .build();

        mockMvc.perform(put(habitAssignLink + "/saveShoppingListForHabitAssign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        verify(habitAssignService).updateUserShoppingListItem(updateDto);
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
