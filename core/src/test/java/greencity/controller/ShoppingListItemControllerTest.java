package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.GreenCityApplication;
import greencity.ModelUtils;
import greencity.config.SecurityConfig;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.enums.ShoppingListItemStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserShoppingListItemNotSavedException;
import greencity.service.LanguageService;
import greencity.service.ShoppingListItemService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ShoppingListItemController.class)
@ContextConfiguration(classes = {GreenCityApplication.class})
public class ShoppingListItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    ModelMapper modelMapper;

    @MockBean
    LanguageService languageService;

    @MockBean
    private ShoppingListItemService shoppingListItemService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        when(languageService.findAllLanguageCodes()).thenReturn(Arrays.asList("en", "ua"));
        when(userService.findByEmail(eq("user@example.com"))).thenReturn(ModelUtils.getUserVO());
    }

    @Test
    public void testSaveUserShoppingListItems_Success() throws Exception {
        List<ShoppingListItemRequestDto> requestDtoList = Collections.singletonList(new ShoppingListItemRequestDto(1L));
        String jsonRequest = objectMapper.writeValueAsString(requestDtoList);

        UserShoppingListItemResponseDto responseDto = ModelUtils.getUserShoppingListItemResponseDto();
        List<UserShoppingListItemResponseDto> responseList = Collections.singletonList(responseDto);

        when(shoppingListItemService.saveUserShoppingListItems(eq(1L), eq(1L), anyList(), eq("en")))
                .thenReturn(responseList);

        mockMvc.perform(post("/user/shopping-list-items?habitId=1")
                        .with(user("user@example.com"))
                        .with(csrf())
                        .header("Accept-Language", "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(requestDtoList.size())))
                .andExpect(jsonPath("$[0].id", is(responseDto.getId().intValue())));

        verify(shoppingListItemService, times(1)).saveUserShoppingListItems(eq(1L), eq(1L), anyList(), eq("en"));
    }

    @Test
    public void testSaveUserShoppingListItems_Failure_WithCustomException() throws Exception {
        List<ShoppingListItemRequestDto> requestDtoList = Collections.singletonList(new ShoppingListItemRequestDto(1L));
        String jsonRequest = objectMapper.writeValueAsString(requestDtoList);

        when(shoppingListItemService.saveUserShoppingListItems(eq(1L), eq(1L), anyList(), eq("en")))
                .thenThrow(new UserShoppingListItemNotSavedException("Couldn't save user shopping list item."));

        mockMvc.perform(post("/user/shopping-list-items?habitId=1")
                        .with(user("user@example.com"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Couldn't save user shopping list item.")));
    }

    @Test
    public void testSaveUserShoppingListItems_Forbidden() throws Exception {
        List<ShoppingListItemRequestDto> requestDtoList = Collections.singletonList(new ShoppingListItemRequestDto(1L));
        String jsonRequest = objectMapper.writeValueAsString(requestDtoList);

        when(shoppingListItemService.saveUserShoppingListItems(eq(1L), eq(1L), anyList(), eq("en")))
                .thenThrow(new AccessDeniedException("Forbidden"));

        mockMvc.perform(post("/user/shopping-list-items?habitId=1")
                        .with(user("user@example.com"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetShoppingListItemsAssignedToUser_Success() throws Exception {
        UserShoppingListItemResponseDto responseDto = ModelUtils.getUserShoppingListItemResponseDto();
        List<UserShoppingListItemResponseDto> responseList = Collections.singletonList(responseDto);

        when(shoppingListItemService.getUserShoppingList(eq(1L), eq(1L), eq("en")))
                .thenReturn(responseList);

        mockMvc.perform(get("/user/shopping-list-items/habits/1/shopping-list")
                        .with(user("user@example.com")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(responseList.size())))
                .andExpect(jsonPath("$[0].id", is(responseDto.getId().intValue())))
                .andExpect(jsonPath("$[0].text", is(responseDto.getText())));

        verify(shoppingListItemService, times(1)).getUserShoppingList(eq(1L), eq(1L), eq("en"));
    }

    @Test
    public void testGetShoppingListItemsAssignedToUser_Forbidden() throws Exception {
        when(shoppingListItemService.getUserShoppingList(eq(1L), eq(1L), eq("en")))
                .thenThrow(new AccessDeniedException("Forbidden"));

        mockMvc.perform(get("/user/shopping-list-items/habits/1/shopping-list")
                        .with(user("user@example.com"))
                        .header("Accept-Language", "en"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testBulkDeleteUserShoppingListItems_Success() throws Exception {
        String validIds = "1,2,3";
        List<Long> deletedIds = Arrays.asList(1L, 2L, 3L);

        when(shoppingListItemService.deleteUserShoppingListItems(eq(validIds)))
                .thenReturn(deletedIds);

        mockMvc.perform(delete("/user/shopping-list-items/user-shopping-list-items")
                        .with(user("user@example.com"))
                        .with(csrf())
                        .param("ids", validIds))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(deletedIds.size())))
                .andExpect(jsonPath("$[0]", is(1)))
                .andExpect(jsonPath("$[1]", is(2)))
                .andExpect(jsonPath("$[2]", is(3)));

        verify(shoppingListItemService, times(1)).deleteUserShoppingListItems(eq(validIds));
    }

    @Test
    public void testBulkDeleteUserShoppingListItems_InvalidIds_BadRequest() throws Exception {
        String invalidIds = "1 2 3";

        mockMvc.perform(delete("/user/shopping-list-items/user-shopping-list-items")
                        .with(user("user@example.com"))
                        .with(csrf())
                        .param("ids", invalidIds))
                .andExpect(status().isBadRequest());

        verify(shoppingListItemService, never()).deleteUserShoppingListItems(eq(invalidIds));
    }

    @Test
    public void testDeleteUserShoppingListItem_Success() throws Exception {
        mockMvc.perform(delete("/user/shopping-list-items")
                        .with(user("user@example.com"))
                        .with(csrf())
                        .param("habitId", "1")
                        .param("shoppingListItemId", "1"))
                .andExpect(status().isOk());

        verify(shoppingListItemService, times(1))
                .deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(1L, 1L, 1L);
    }

    @Test
    public void testDeleteUserShoppingListItem_Forbidden() throws Exception {
        doThrow(new AccessDeniedException("Item not found"))
                .when(shoppingListItemService)
                .deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(1L, 1L, 1L);

        mockMvc.perform(delete("/user/shopping-list-items")
                        .with(user("user@example.com"))
                        .with(csrf())
                        .param("habitId", "1")
                        .param("shoppingListItemId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteUserShoppingListItem_BadRequest() throws Exception {
        doThrow(new BadRequestException("Bad Request"))
                .when(shoppingListItemService)
                .deleteUserShoppingListItemByItemIdAndUserIdAndHabitId(999L, 1L, 1L);

        mockMvc.perform(delete("/user/shopping-list-items")
                        .with(user("user@example.com"))
                        .with(csrf())
                        .param("habitId", "1")
                        .param("shoppingListItemId", "999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUserShoppingListItem_Success() throws Exception {
        UserShoppingListItemResponseDto responseDto = ModelUtils.getUserShoppingListItemResponseDto();
        responseDto.setStatus(ShoppingListItemStatus.DONE);

        when(shoppingListItemService.updateUserShopingListItemStatus(eq(1L), eq(1L), eq("en")))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/user/shopping-list-items/1")
                        .with(user("user@example.com"))
                        .with(csrf())
                        .header("Accept-Language", "en"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(responseDto.getId().intValue())))
                .andExpect(jsonPath("$.status", is(responseDto.getStatus().toString())));

        verify(shoppingListItemService, times(1))
                .updateUserShopingListItemStatus(eq(1L), eq(1L), eq("en"));
    }

    @Test
    public void testUpdateUserShoppingListItemStatus_Success() throws Exception {
        UserShoppingListItemResponseDto dto = ModelUtils.getUserShoppingListItemResponseDto();
        dto.setStatus(ShoppingListItemStatus.DONE);
        List<UserShoppingListItemResponseDto> responseDto = List.of(dto);

        when(shoppingListItemService.updateUserShoppingListItemStatus(eq(1L), eq(1L), eq("en"), eq("DONE")))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/user/shopping-list-items/{userShoppingListItemId}/status/{status}",
                        responseDto.get(0).getId(), responseDto.get(0).getStatus().toString())
                .with(user("user@example.com"))
                .with(csrf())
                .header("Accept-Language", "en"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseDto.get(0).getId().intValue())))
                .andExpect(jsonPath("$[0].status", is(responseDto.get(0).getStatus().toString())));
    }

    @Test
    public void testUpdateUserShoppingListItemStatus_Unauthorized_Forbidden() throws Exception {
        when(shoppingListItemService.updateUserShopingListItemStatus(eq(1L), eq(2L), eq("en")))
                .thenThrow(new AccessDeniedException("Forbidden"));

        mockMvc.perform(patch("/user/shopping-list-items/2")
                        .with(user("user@example.com"))
                        .with(csrf())
                        .header("Accept-Language", "en"))
                .andExpect(status().isForbidden());

        verify(shoppingListItemService, times(1))
                .updateUserShopingListItemStatus(eq(1L), eq(2L), eq("en"));
    }

    @Test
    public void testFindInProgressByUserId_Success() throws Exception {
        ShoppingListItemDto dto = new ShoppingListItemDto();
        dto.setId(1L);
        dto.setStatus(ShoppingListItemStatus.INPROGRESS.toString());
        List<ShoppingListItemDto> responseList = Collections.singletonList(dto);

        when(shoppingListItemService.findInProgressByUserIdAndLanguageCode(eq(1L), eq("en")))
                .thenReturn(responseList);

        mockMvc.perform(get("/user/shopping-list-items/1/get-all-inprogress")
                        .with(user("user@example.com"))
                        .param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseList.getFirst().getId().intValue())));

        verify(shoppingListItemService, times(1))
                .findInProgressByUserIdAndLanguageCode(eq(1L), eq("en"));
    }

    @Test
    public void testFindInProgressByUserId_InvalidLanguage_BadRequest() throws Exception {
        mockMvc.perform(get("/user/shopping-list-items/2/get-all-inprogress")
                        .with(user("user@example.com"))
                        .header("Accept-Language", "fr"))
                .andExpect(status().isBadRequest());

        verify(shoppingListItemService, never())
                .findInProgressByUserIdAndLanguageCode(anyLong(), eq("fr"));
    }
}
