package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.GreenCityApplication;
import greencity.ModelUtils;
import greencity.config.SecurityConfig;
import greencity.config.WebMvcConfig;
import greencity.dto.PageableDto;
import greencity.dto.friend.SearchFriendDtoResponse;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.security.jwt.JwtTool;
import greencity.service.FriendService;
import greencity.service.UserService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FriendController.class)
@ContextConfiguration(classes = {GreenCityApplication.class})
@Import({SecurityConfig.class, CustomExceptionHandler.class, WebMvcConfig.class})
class FriendControllerTest {

    private static final UserVO USER = ModelUtils.getUserVO();
    private static final String USER_EMAIL = "test@gmail.com";
    private static final String NAME = "test";
    private static final Long FRIEND_ID = 1L;
    private static final Boolean IS_THE_SAME_CITY = false;
    private static final Boolean IS_FRIENDS_OF_FRIENDS = false;

    @MockBean
    private FriendService friendService;
    @MockBean
    private UserService userService;
    @MockBean
    private ModelMapper modelMapper;
    @MockBean
    private JwtTool jwtTool;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        when(userService.findByEmail(USER_EMAIL)).thenReturn(USER);
        reset(modelMapper);
        reset(jwtTool);
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void testSearchNewFriends() throws Exception {
        SearchFriendDtoResponse searchFriendDtoResponse = ModelUtils.getSearchFriendDtoResponse();
        PageableDto<SearchFriendDtoResponse> pageableDto = new PageableDto<>(
            List.of(searchFriendDtoResponse), 1, 0, 1);

        when(friendService.searchNewFriends(eq(NAME), eq(IS_THE_SAME_CITY), eq(IS_FRIENDS_OF_FRIENDS),
            eq(USER), any())).thenReturn(pageableDto);

        mockMvc.perform(get("/friends/not-friends-yet")
                .accept(MediaType.APPLICATION_JSON)
                .param("name", NAME))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(pageableDto)));

        verify(friendService).searchNewFriends(eq(NAME), eq(IS_THE_SAME_CITY),
            eq(IS_FRIENDS_OF_FRIENDS), eq(USER), any());
    }

    @Test
    void testSearchNewFriends_whenUnauthorized_shouldReturn403() throws Exception {
        mockMvc.perform(get("/friends/not-friends-yet"))
            .andExpect(status().isUnauthorized());

        verify(friendService, never()).searchNewFriends(eq(NAME), eq(IS_THE_SAME_CITY),
            eq(IS_FRIENDS_OF_FRIENDS), eq(USER), any());
    }

    @ParameterizedTest
    @WithMockUser(username = USER_EMAIL)
    @ValueSource(strings = {"", "@", "3", "..............................."})
    void testSearchNewFriends_withInvalidName_shouldReturn400(String name) throws Exception {
        mockMvc.perform(get("/friends/not-friends-yet")
                .param("name", name))
            .andExpect(status().isBadRequest());

        verify(friendService, never()).searchNewFriends(eq(name), eq(IS_THE_SAME_CITY),
            eq(IS_FRIENDS_OF_FRIENDS), eq(USER), any());
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void testSearchNewFriends_whenNotExistedUser_shouldReturn404() throws Exception {
        when(friendService.searchNewFriends(eq(NAME), eq(IS_THE_SAME_CITY), eq(IS_FRIENDS_OF_FRIENDS),
            eq(USER), any())).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/friends/not-friends-yet")
                .param("name", NAME))
            .andExpect(status().isNotFound());

        verify(friendService).searchNewFriends(eq(NAME), eq(IS_THE_SAME_CITY),
            eq(IS_FRIENDS_OF_FRIENDS), eq(USER), any());
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void testAddFriend() throws Exception {
        doNothing().when(friendService).addFriend(USER.getId(), FRIEND_ID);

        mockMvc.perform(post("/friends/" + FRIEND_ID))
            .andExpect(status().isOk());

        verify(friendService).addFriend(USER.getId(), FRIEND_ID);
        verify(userService).findByEmail(USER_EMAIL);
    }

    @Test
    void testAddFriend_whenUnauthorized_shouldReturn403() throws Exception {
        mockMvc.perform(post("/friends/" + FRIEND_ID))
            .andExpect(status().isUnauthorized());

        verify(friendService, never()).addFriend(USER.getId(), FRIEND_ID);
    }

    @ParameterizedTest
    @WithMockUser(username = USER_EMAIL)
    @ValueSource(longs = {-1, 0})
    void testAddFriend_withInvalidFriendId_shouldReturn400(Long friendId) throws Exception {
        mockMvc.perform(post("/friends/" + friendId))
            .andExpect(status().isBadRequest());

        verify(friendService, never()).addFriend(USER.getId(), friendId);
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void testAddFriend_whenNotExistedUser_shouldReturn404() throws Exception {
        doThrow(NotFoundException.class).when(friendService).addFriend(USER.getId(), FRIEND_ID);

        mockMvc.perform(post("/friends/" + FRIEND_ID))
            .andExpect(status().isNotFound());

        verify(friendService).addFriend(USER.getId(), FRIEND_ID);
        verify(userService).findByEmail(USER_EMAIL);
    }
  
    @Test
    @WithMockUser(username = "vovasaenco@ukr.net")
    public void removeFriend_FriendshipDeleted() throws Exception {

        long friendId = 1L;
        String email = "vovasaenco@ukr.net";

        UserVO userVO = new UserVO();
        userVO.setId(123L);

        when(userService.findByEmail(email)).thenReturn(userVO);

        mockMvc.perform(delete("/friends/" + friendId))
                .andExpect(status().isOk());

        verify(userService, times(1)).findByEmail(email);
        verify(friendService, times(1)).removeFriend(userVO.getId(), friendId);
    }

    @Test
    @WithMockUser(username = "vovasaenco@ukr.net")
    public void removeFriend_FriendshipNotExists_NotFoundExceptionThrown() throws Exception {

        long friendId = 1L;
        String email = "vovasaenco@ukr.net";

        UserVO userVO = new UserVO();
        userVO.setId(123L);

        when(userService.findByEmail(email)).thenReturn(userVO);

        doThrow(new NotFoundException("Friendship not found"))
                .when(friendService).removeFriend(userVO.getId(), friendId);

        mockMvc.perform(delete("/friends/" + friendId))
                .andExpect(status().isNotFound());

        verify(friendService, times(1)).removeFriend(userVO.getId(), friendId);
    }
}
