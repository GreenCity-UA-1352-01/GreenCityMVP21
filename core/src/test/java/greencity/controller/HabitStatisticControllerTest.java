package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greencity.GreenCityApplication;
import greencity.config.SecurityConfig;
import greencity.dto.habitstatistic.*;
import greencity.dto.user.UserVO;
import greencity.enums.HabitRate;
import greencity.exception.exceptions.NotFoundException;
import greencity.security.jwt.JwtTool;
import greencity.service.HabitStatisticService;
import greencity.service.LanguageService;
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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HabitStatisticController.class)
@ContextConfiguration(classes = {GreenCityApplication.class, SecurityConfig.class})
public class HabitStatisticControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HabitStatisticService habitStatisticService;

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
    public void findAllByHabitId_Correct_StatisticReturned() throws Exception {

        Long habitId = 1L;

        var getHabitStatisticDto = GetHabitStatisticDto
                .builder()
                .habitStatisticDtoList(new ArrayList<>())
                .amountOfUsersAcquired(1L)
                .build();

        when(habitStatisticService.findAllStatsByHabitId(habitId))
                .thenReturn(getHabitStatisticDto);

        mockMvc.perform(get("/habit/statistic/" + habitId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.habitStatisticDtoList").isArray())
                .andExpect(jsonPath("$.habitStatisticDtoList.length()").value(0))
                .andExpect(jsonPath("$.amountOfUsersAcquired").value(1));

        verify(habitStatisticService, times(1)).findAllStatsByHabitId(habitId);
    }

    @Test
    public void findAllByHabitId_NotExistingId_NotFoundExceptionThrown() throws Exception {

        Long habitId = 1000L;

        when(habitStatisticService.findAllStatsByHabitId(habitId))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/habit/statistic/" + habitId).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(status().isNotFound());

        verify(habitStatisticService, times(1)).findAllStatsByHabitId(habitId);
    }

    @Test
    public void findAllStatsByHabitAssignId_Correct_StatisticListReturned() throws Exception {

        Long habitAssignId = 1L;

        var habitStatistic = HabitStatisticDto
                .builder()
                .id(1L)
                .habitRate(HabitRate.DEFAULT)
                .createDate(ZonedDateTime.now())
                .amountOfItems(0)
                .habitAssignId(habitAssignId)
                .build();

        List<HabitStatisticDto> habitStatisticDtoList = new ArrayList<>();
        habitStatisticDtoList.add(habitStatistic);


        when(habitStatisticService.findAllStatsByHabitAssignId(habitAssignId))
                .thenReturn(habitStatisticDtoList);

        mockMvc.perform(get("/habit/statistic/assign/" + habitAssignId).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].habitRate").value("DEFAULT"))
                .andExpect(jsonPath("$[0].amountOfItems").value(0))
                .andExpect(jsonPath("$[0].habitAssignId").value(1))
                .andExpect(jsonPath("$[0].createDate").exists())
                .andExpect(status().isOk());

        verify(habitStatisticService, times(1)).findAllStatsByHabitAssignId(habitAssignId);

    }

    @Test
    public void findAllStatsByHabitAssignId_NotExistingId_NotFoundExceptionThrown() throws Exception {

        Long habitAssignId = 1L;

        when(habitStatisticService.findAllStatsByHabitAssignId(habitAssignId))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/habit/statistic/assign/" + habitAssignId).accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(status().isNotFound());

        verify(habitStatisticService, times(1)).findAllStatsByHabitAssignId(habitAssignId);
    }

    @Test
    public void getTodayStatisticsForAllHabitItems_IncorrectLang_BadRequestThrown() throws Exception {

        String wrongLanguage = "fr";

        when(languageService.findAllLanguageCodes())
                .thenReturn(List.of("ua", "en"));

        mockMvc.perform(get("/habit/statistic/todayStatisticsForAllHabitItems")
                        .param("lang", wrongLanguage)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(habitStatisticService);
    }

    @Test
    public void getTodayStatisticsForAllHabitItems_CorrectLang_StatisticReturned() throws Exception {

        String lang = "ua";

        var habit = HabitItemsAmountStatisticDto
                .builder()
                .habitItem("Habit #1")
                .notTakenItems(1L)
                .build();

        when(languageService.findAllLanguageCodes())
                .thenReturn(List.of("ua", "en"));

        when(habitStatisticService.getTodayStatisticsForAllHabitItems(lang))
                .thenReturn(List.of(habit));


        mockMvc.perform(get("/habit/statistic/todayStatisticsForAllHabitItems")
                        .param("lang", lang)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].habitItem").value("Habit #1"))
                .andExpect(jsonPath("$[0].notTakenItems").value(1))
                .andExpect(status().isOk());

        verify(habitStatisticService, times(1)).getTodayStatisticsForAllHabitItems(lang);
    }

    @Test
    @WithMockUser
    public void findAmountOfAcquiredHabits_Correct_StatisticReturned() throws Exception {

        Long userId = 1L;

        when(habitStatisticService.getAmountOfAcquiredHabitsByUserId(userId))
                .thenReturn(10L);

        mockMvc.perform(get("/habit/statistic/acquired/count")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("10"))
                .andExpect(status().isOk());

        verify(habitStatisticService, times(1)).getAmountOfAcquiredHabitsByUserId(userId);
    }

    @Test
    public void findAmountOfAcquiredHabits_Unauthorized_UnauthorizedExceptionThrown() throws Exception {

        Long userId = 1L;

        mockMvc.perform(get("/habit/statistic/acquired/count")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(habitStatisticService);
    }

    @Test
    @WithMockUser
    public void findAmountOfHabitsInProgress_Correct_StatisticReturned() throws Exception {

        Long userId = 1L;

        when(habitStatisticService.getAmountOfHabitsInProgressByUserId(userId))
                .thenReturn(5L);

        mockMvc.perform(get("/habit/statistic/in-progress/count")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("5"))
                .andExpect(status().isOk());

        verify(habitStatisticService, times(1)).getAmountOfHabitsInProgressByUserId(userId);
    }

    @Test
    public void findAmountOfHabitsInProgress_Unauthorized_UnauthorizedExceptionThrown() throws Exception {

        Long userId = 1L;

        mockMvc.perform(get("/habit/statistic/in-progress/count")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(habitStatisticService);
    }

    @Test
    public void saveHabitStatistic_Unauthorized_UnauthorizedExceptionThrown() throws Exception {

        long habitId = 10000L;

        var addHabitStatisticDto = AddHabitStatisticDto
                .builder()
                .amountOfItems(-1)
                .habitRate(HabitRate.DEFAULT)
                .createDate(ZonedDateTime.now())
                .build();

        mockMvc.perform(post("/habit/statistic/" + habitId)
                        .content(objectMapper.writeValueAsString(addHabitStatisticDto)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(habitStatisticService);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"BAD_ROLE"})
    public void saveHabitStatistic_BadRole_ForbiddenExceptionThrown() throws Exception {

        long habitId = 10000L;

        var addHabitStatisticDto = AddHabitStatisticDto
                .builder()
                .amountOfItems(-1)
                .habitRate(HabitRate.DEFAULT)
                .createDate(ZonedDateTime.now())
                .build();

        mockMvc.perform(post("/habit/statistic/" + habitId)
                        .content(objectMapper.writeValueAsString(addHabitStatisticDto)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(habitStatisticService);
    }

    @Test
    @WithMockUser
    public void saveHabitStatistic_NotValidContent_BadRequestExceptionThrown() throws Exception {

        long habitId = 10000L;

        var addHabitStatisticDto = AddHabitStatisticDto
                .builder()
                .amountOfItems(-1)
                .habitRate(HabitRate.DEFAULT)
                .createDate(ZonedDateTime.now())
                .build();

        mockMvc.perform(post("/habit/statistic/" + habitId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addHabitStatisticDto)))
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(habitStatisticService);
    }

    @Test
    @WithMockUser(username = "user@example.com")
    public void saveHabitStatistic_NotExistingHabitId_NotFoundExceptionThrown() throws Exception {

        long habitId = 10000L;
        String email = "user@example.com";

        var addHabitStatisticDto = AddHabitStatisticDto
                .builder()
                .amountOfItems(1)
                .habitRate(HabitRate.DEFAULT)
                .createDate(ZonedDateTime.now())
                .build();

        UserVO userVO = new UserVO();
        userVO.setId(123L);

        when(userService.findByEmail(email)).thenReturn(userVO);

        when(habitStatisticService.saveByHabitIdAndUserId(anyLong(), any(), any(AddHabitStatisticDto.class)))
                .thenThrow(NotFoundException.class);


        mockMvc.perform(post("/habit/statistic/" + habitId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addHabitStatisticDto)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findByEmail(email);
        verify(habitStatisticService, times(1)).saveByHabitIdAndUserId(anyLong(), any(), any(AddHabitStatisticDto.class));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    public void saveHabitStatistic_Correct_StatisticCreated() throws Exception {

        long habitId = 1L;
        String email = "user@example.com";

        var addHabitStatisticDto = AddHabitStatisticDto
                .builder()
                .amountOfItems(1)
                .habitRate(HabitRate.DEFAULT)
                .createDate(ZonedDateTime.now())
                .build();

        var habitStatistic = HabitStatisticDto
                .builder()
                .id(1L)
                .habitRate(HabitRate.DEFAULT)
                .createDate(ZonedDateTime.now())
                .amountOfItems(1)
                .habitAssignId(1L)
                .build();

        UserVO userVO = new UserVO();
        userVO.setId(123L);

        when(userService.findByEmail(email)).thenReturn(userVO);

        when(habitStatisticService.saveByHabitIdAndUserId(anyLong(), any(), any(AddHabitStatisticDto.class)))
                .thenReturn(habitStatistic);

        mockMvc.perform(post("/habit/statistic/" + habitId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addHabitStatisticDto)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.habitRate").value("DEFAULT"))
                .andExpect(jsonPath("$.createDate").exists())
                .andExpect(jsonPath("$.amountOfItems").value(1))
                .andExpect(jsonPath("$.habitAssignId").value(1))
                .andExpect(status().isCreated());

        verify(userService, times(1)).findByEmail(email);
        verify(habitStatisticService, times(1)).saveByHabitIdAndUserId(anyLong(), any(), any(AddHabitStatisticDto.class));
    }

    @Test
    public void updateStatistic_Unauthorized_UnauthorizedExceptionThrown() throws Exception {

        long habitStatisticId = 1L;

        var updateHabitStatisticDto = UpdateHabitStatisticDto
                .builder()
                .amountOfItems(1)
                .habitRate(HabitRate.DEFAULT)
                .build();

        mockMvc.perform(put("/habit/statistic/" + habitStatisticId)
                        .content(objectMapper.writeValueAsString(updateHabitStatisticDto)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(habitStatisticService);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"BAD_ROLE"})
    public void updateStatistic_BadRole_ForbiddenExceptionThrown() throws Exception {

        long habitStatisticId = 1L;

        var updateHabitStatisticDto = UpdateHabitStatisticDto
                .builder()
                .amountOfItems(1)
                .habitRate(HabitRate.DEFAULT)
                .build();

        mockMvc.perform(put("/habit/statistic/" + habitStatisticId)
                        .content(objectMapper.writeValueAsString(updateHabitStatisticDto)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(habitStatisticService);
    }

    @Test
    @WithMockUser
    public void updateStatistic_NotValidContent_BadRequestExceptionThrown() throws Exception {

        long habitStatisticId = 1L;

        var updateHabitStatisticDto = UpdateHabitStatisticDto
                .builder()
                .amountOfItems(-1)
                .habitRate(HabitRate.DEFAULT)
                .build();

        mockMvc.perform(put("/habit/statistic/" + habitStatisticId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateHabitStatisticDto)))
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(status().isBadRequest());

        verifyNoInteractions(habitStatisticService);
    }

    @Test
    @WithMockUser(username = "user@example.com")
    public void updateStatistic_NotExistingHabitStatisticId_NotFoundExceptionThrown() throws Exception {

        long habitStatisticId = 1L;
        String email = "user@example.com";

        var updateHabitStatisticDto = UpdateHabitStatisticDto
                .builder()
                .amountOfItems(1)
                .habitRate(HabitRate.DEFAULT)
                .build();

        UserVO userVO = new UserVO();
        userVO.setId(123L);

        when(userService.findByEmail(email)).thenReturn(userVO);

        when(habitStatisticService.update(anyLong(), any(), any(UpdateHabitStatisticDto.class)))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(put("/habit/statistic/" + habitStatisticId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateHabitStatisticDto)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findByEmail(email);
        verify(habitStatisticService, times(1)).update(anyLong(), any(), any(UpdateHabitStatisticDto.class));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    public void updateStatistic_Correct_StatisticUpdated() throws Exception {

        long habitId = 1L;
        String email = "user@example.com";

        var updateHabitStatisticDto = UpdateHabitStatisticDto
                .builder()
                .amountOfItems(1)
                .habitRate(HabitRate.DEFAULT)
                .build();

        UserVO userVO = new UserVO();
        userVO.setId(123L);

        when(userService.findByEmail(email)).thenReturn(userVO);

        when(habitStatisticService.update(anyLong(), any(), any(UpdateHabitStatisticDto.class)))
                .thenReturn(updateHabitStatisticDto);

        mockMvc.perform(put("/habit/statistic/" + habitId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateHabitStatisticDto)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amountOfItems").value(1))
                .andExpect(jsonPath("$.habitRate").value("DEFAULT"))
                .andExpect(status().isOk());

        verify(userService, times(1)).findByEmail(email);
        verify(habitStatisticService, times(1)).update(anyLong(), any(), any(UpdateHabitStatisticDto.class));
    }
}
