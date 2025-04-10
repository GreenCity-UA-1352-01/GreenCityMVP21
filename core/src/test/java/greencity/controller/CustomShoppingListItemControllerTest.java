package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.enums.ShoppingListItemStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.CustomShoppingListItemService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static greencity.ModelUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomShoppingListItemControllerTest {

    private static final String CONTROLLER_URL = "custom/shopping-list-items";
    public static final Long USER_ID = 2L;
    public static final Long HABIT_ID = 1L;
    public static final Long ITEM_ID = 1L;
    public static final ShoppingListItemStatus STATUS = ShoppingListItemStatus.ACTIVE;
    public static final String INVALID_VALUE = "------";

    @Mock
    private CustomShoppingListItemService customShoppingListItemService;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private CustomShoppingListItemController customShoppingListItemController;

    //    private Principal principal = getPrincipal();
    private ErrorAttributes errorAttributes = new DefaultErrorAttributes();
    private MockMvc mockMvc;

//    @BeforeAll
//    public static void beforeAll() {
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
//        factory.close();
//    }

    @BeforeEach
    void beforeEach() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(customShoppingListItemController)
//                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
//                        new UserArgumentResolver(userService, modelMapper))
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .setValidator(new LocalValidatorFactoryBean())
                .build();
    }

    @Test
    void getAllAvailableCustomShoppingListItems() throws Exception {
        var resultItem = getCustomShoppingListItemResponseDto();
        var expectedResult = Collections.singletonList(resultItem);

        when(customShoppingListItemService.findAllAvailableCustomShoppingListItems(anyLong(), anyLong()))
                .thenReturn(expectedResult);

        mockMvc.perform(get("/{url}/{userId}/{habitId}", CONTROLLER_URL, USER_ID, HABIT_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));

        verify(customShoppingListItemService).findAllAvailableCustomShoppingListItems(USER_ID, HABIT_ID);
    }

    @Test
    void getAllAvailableCustomShoppingListItems_withInvalidUserId_shouldReturn400() throws Exception {
        checkRequestWithInvalidParams(HttpMethod.GET, "/{url}/{userId}/{habitId}",
                CONTROLLER_URL, INVALID_VALUE, HABIT_ID);
    }

    @Test
    void getAllAvailableCustomShoppingListItems_withInvalidHabitId_shouldReturn400() throws Exception {
        checkRequestWithInvalidParams(HttpMethod.GET, "/{url}/{userId}/{habitId}",
                CONTROLLER_URL, USER_ID, INVALID_VALUE);
    }

    @Test
    void saveUserCustomShoppingListItems() throws Exception {
        String content = "{"
                + "  \"customShoppingListItemSaveRequestDtoList\":"
                + "    ["
                + "      {"
                + "        \"text\": \"text\""
                + "      }"
                + "    ]"
                + "}";
        BulkSaveCustomShoppingListItemDto dto = objectMapper.readValue(content,
                BulkSaveCustomShoppingListItemDto.class);

        var resultItem = getCustomShoppingListItemResponseDto();
        var expectedResult = Collections.singletonList(resultItem);

        when(customShoppingListItemService.save(any(BulkSaveCustomShoppingListItemDto.class), anyLong(), anyLong()))
                .thenReturn(expectedResult);

        mockMvc.perform(post("/{url}/{userId}/{habitAssignId}/custom-shopping-list-items",
                        CONTROLLER_URL, USER_ID, HABIT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));

        verify(customShoppingListItemService).save(dto, USER_ID, HABIT_ID);
    }

    @Test
    void saveUserCustomShoppingListItems_withInvalidUserId_shouldReturn400() throws Exception {
        checkRequestWithInvalidParams(HttpMethod.POST, "/{url}/{userId}/{habitAssignId}/custom-shopping-list-items",
                CONTROLLER_URL, INVALID_VALUE, HABIT_ID);
    }

    @Test
    void saveUserCustomShoppingListItems_withInvalidHabitAssignId_shouldReturn400() throws Exception {
        checkRequestWithInvalidParams(HttpMethod.POST, "/{url}/{userId}/{habitAssignId}/custom-shopping-list-items",
                CONTROLLER_URL, USER_ID, INVALID_VALUE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"other\"", "\"1\"", "text", "1"})
    void saveUserCustomShoppingListItems_withInvalidData_shouldReturn400(String fieldName) throws Exception {
        String content = "{"
                + "  \"customShoppingListItemSaveRequestDtoList\":"
                + "    ["
                + "      {"
                + "        " + fieldName + ": \"text\""
                + "      }"
                + "    ]"
                + "}";

        mockMvc.perform(post("/{url}/{userId}/{habitAssignId}/custom-shopping-list-items",
                        CONTROLLER_URL, USER_ID, HABIT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customShoppingListItemService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"\"", "\"   \""})
    void saveUserCustomShoppingListItems_withBlankValues_shouldReturn400(String fieldValue) throws Exception {
        String content = "{"
                + "  \"customShoppingListItemSaveRequestDtoList\":"
                + "    ["
                + "      {"
                + "        \"text\": " + fieldValue
                + "      }"
                + "    ]"
                + "}";

        mockMvc.perform(post("/{url}/{userId}/{habitAssignId}/custom-shopping-list-items",
                        CONTROLLER_URL, USER_ID, HABIT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customShoppingListItemService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"active", "done", "disabled", "inprogress"})
    void updateItemStatus(String itemStatus) throws Exception {
        var expectedResult = getCustomShoppingListItemResponseDto();
        expectedResult.setStatus(ShoppingListItemStatus.valueOf(itemStatus.toUpperCase()));

        when(customShoppingListItemService.updateItemStatus(anyLong(), anyLong(), anyString()))
                .thenReturn(expectedResult);

        mockMvc.perform(patch("/{url}/{userId}/custom-shopping-list-items", CONTROLLER_URL, USER_ID)
                        .param("itemId", ITEM_ID.toString())
                        .param("status", itemStatus)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));

        verify(customShoppingListItemService).updateItemStatus(USER_ID, ITEM_ID, itemStatus);
    }

    @Test
    void updateItemStatus_withInvalidUserId_shouldReturn400() throws Exception {
        checkRequestWithInvalidParams(HttpMethod.PATCH,
                mapOf("itemId", ITEM_ID.toString(), "status", STATUS.toString()),
                "/{url}/{userId}/custom-shopping-list-items",
                CONTROLLER_URL, INVALID_VALUE);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {INVALID_VALUE})
    void updateItemStatus_withInvalidItemId_shouldReturn400(String itemId) throws Exception {
        checkRequestWithInvalidParams(HttpMethod.PATCH, mapOf("itemId", itemId, "status", STATUS.toString()),
                "/{url}/{userId}/custom-shopping-list-items",
                CONTROLLER_URL, USER_ID);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {INVALID_VALUE})
    void updateItemStatus_withInvalidStatus_shouldReturn400(String status) throws Exception {
        when(customShoppingListItemService.updateItemStatus(anyLong(), anyLong(), nullable(String.class)))
                .thenThrow(BadRequestException.class);

        mockMvc.perform(patch("/{url}/{userId}/custom-shopping-list-items", CONTROLLER_URL, USER_ID)
                        .param("itemId", ITEM_ID.toString())
                        .param("status", status)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(customShoppingListItemService, atMostOnce()).updateItemStatus(USER_ID, ITEM_ID, status);
    }

    @Test
    void updateItemStatus_withNotExistingItem_shouldReturn404() throws Exception {
        when(customShoppingListItemService.updateItemStatus(anyLong(), anyLong(), anyString()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(patch("/{url}/{userId}/custom-shopping-list-items", CONTROLLER_URL, USER_ID)
                        .param("itemId", ITEM_ID.toString())
                        .param("status", STATUS.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(customShoppingListItemService).updateItemStatus(USER_ID, ITEM_ID, STATUS.toString());
    }

    @Test
    void updateItemStatusToDone() throws Exception {
        doNothing().when(customShoppingListItemService).updateItemStatusToDone(anyLong(), anyLong());

        mockMvc.perform(patch("/{url}/{userId}/done", CONTROLLER_URL, USER_ID)
                        .param("itemId", ITEM_ID.toString()))
                .andExpect(status().isOk());

        verify(customShoppingListItemService).updateItemStatusToDone(USER_ID, ITEM_ID);
    }

    @Test
    void updateItemStatusToDone_withInvalidUserId_shouldReturn400() throws Exception {
        checkRequestWithInvalidParams(HttpMethod.PATCH, mapOf("itemId", ITEM_ID.toString()),
                "/{url}/{userId}/done",
                CONTROLLER_URL, INVALID_VALUE);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {INVALID_VALUE})
    void updateItemStatusToDone_withInvalidItemId_shouldReturn400(String itemId) throws Exception {
        checkRequestWithInvalidParams(HttpMethod.PATCH, mapOf("itemId", itemId),
                "/{url}/{userId}/done",
                CONTROLLER_URL, USER_ID);
    }

    @Test
    void updateItemStatusToDone_withNotExistingItem_shouldReturn404() throws Exception {
        doThrow(NotFoundException.class)
                .when(customShoppingListItemService).updateItemStatusToDone(anyLong(), anyLong());

        mockMvc.perform(patch("/{url}/{userId}/done", CONTROLLER_URL, USER_ID)
                        .param("itemId", ITEM_ID.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(customShoppingListItemService).updateItemStatusToDone(USER_ID, ITEM_ID);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "1,2", "1,2,3"})
    void bulkDeleteCustomShoppingListItems(String ids) throws Exception {
        var expectedResult = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();

        when(customShoppingListItemService.bulkDelete(anyString()))
                .thenReturn(expectedResult);

        mockMvc.perform(delete("/{url}/{userId}/custom-shopping-list-items", CONTROLLER_URL, USER_ID)
                        .param("ids", ids)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));

        verify(customShoppingListItemService).bulkDelete(ids);
    }

    @Test
    void bulkDeleteCustomShoppingListItems_withInvalidUserId_shouldReturn400() throws Exception {
        checkRequestWithInvalidParams(HttpMethod.DELETE, mapOf("ids", ITEM_ID.toString()),
                "/{url}/{userId}/custom-shopping-list-items",
                CONTROLLER_URL, INVALID_VALUE);
    }

    @Test
    void bulkDeleteCustomShoppingListItems_withInvalidItemIds_shouldReturn400() throws Exception {
        when(customShoppingListItemService.bulkDelete(anyString()))
//                .thenThrow(NumberFormatException.class);
                .thenThrow(BadRequestException.class);

        mockMvc.perform(delete("/{url}/{userId}/custom-shopping-list-items", CONTROLLER_URL, USER_ID)
                        .param("ids", INVALID_VALUE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(customShoppingListItemService).bulkDelete(INVALID_VALUE);
    }

    @Test
    void bulkDeleteCustomShoppingListItems_withoutItemIds_shouldReturn400() throws Exception {
        checkRequestWithInvalidParams(HttpMethod.DELETE, "/{url}/{userId}/custom-shopping-list-items",
                CONTROLLER_URL, USER_ID);
    }

    @ParameterizedTest
    @ValueSource(strings = {"active", "done", "disabled", "inprogress"})
    void getAllCustomShoppingItemsByStatus_withStatus_shouldReturn200(String itemStatus) throws Exception {
        var resultItem = getCustomShoppingListItemResponseDto();
        resultItem.setStatus(ShoppingListItemStatus.valueOf(itemStatus.toUpperCase()));
        var expectedResult = Collections.singletonList(resultItem);

        when(customShoppingListItemService.findAllUsersCustomShoppingListItemsByStatus(anyLong(), anyString()))
                .thenReturn(expectedResult);

        mockMvc.perform(get("/{url}/{userId}/custom-shopping-list-items", CONTROLLER_URL, USER_ID)
                        .param("status", itemStatus)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));

        verify(customShoppingListItemService).findAllUsersCustomShoppingListItemsByStatus(USER_ID, itemStatus);
    }

    @Test
    void getAllCustomShoppingItemsByStatus_withoutStatus_shouldReturn200() throws Exception {
        var resultItem = getCustomShoppingListItemResponseDto();
        var expectedResult = Collections.singletonList(resultItem);

        when(customShoppingListItemService.findAllUsersCustomShoppingListItemsByStatus(anyLong(), isNull()))
                .thenReturn(expectedResult);

        mockMvc.perform(get("/{url}/{userId}/custom-shopping-list-items", CONTROLLER_URL, USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));

        verify(customShoppingListItemService).findAllUsersCustomShoppingListItemsByStatus(USER_ID, null);
    }

    @Test
    void getAllCustomShoppingItemsByStatus_withInvalidUserId_shouldReturn400() throws Exception {
        checkRequestWithInvalidParams(HttpMethod.GET, mapOf("status", STATUS.toString()),
                "/{url}/{userId}/custom-shopping-list-items",
                CONTROLLER_URL, INVALID_VALUE);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {INVALID_VALUE})
    void getAllCustomShoppingItemsByStatus_withInvalidStatus_shouldReturn400(String status) throws Exception {
        when(customShoppingListItemService.findAllUsersCustomShoppingListItemsByStatus(anyLong(), nullable(String.class)))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/{url}/{userId}/custom-shopping-list-items", CONTROLLER_URL, USER_ID)
                        .param("status", status)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(customShoppingListItemService).findAllUsersCustomShoppingListItemsByStatus(USER_ID, status);
    }

    private void checkRequestWithInvalidParams(HttpMethod method, String urlTemplate, Object... params) throws Exception {
        checkRequestWithInvalidParams(method, Collections.emptyMap(), urlTemplate, params);
    }

    private void checkRequestWithInvalidParams(HttpMethod method,
                                               Map<String, String> pathVars,
                                               String urlTemplate,
                                               Object... params) throws Exception {
        var request = MockMvcRequestBuilders.request(method, urlTemplate, params)
                .accept(MediaType.APPLICATION_JSON);

        pathVars.forEach(request::param);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customShoppingListItemService);
    }

    private Map<String, String> mapOf(String key, String value, String... values) {
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        for (int i = 0; i < values.length; i += 2) {
            map.put(values[i], values[i + 1]);
        }
        return map;
    }

}
