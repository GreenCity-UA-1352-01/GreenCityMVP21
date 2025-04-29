package greencity.controller;

import greencity.GreenCityApplication;
import greencity.config.SecurityConfig;
import greencity.dto.user.UserVO;
import greencity.security.jwt.JwtTool;
import greencity.service.FriendService;
import greencity.service.UserService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FriendController.class)
@ContextConfiguration(classes = {GreenCityApplication.class, SecurityConfig.class})
public class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FriendService friendService;

    @MockBean
    private UserService userService;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private JwtTool jwtTool;

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
}
