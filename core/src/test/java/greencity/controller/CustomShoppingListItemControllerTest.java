package greencity.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.converters.UserArgumentResolver;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.dto.shoppinglistitem.BulkSaveCustomShoppingListItemDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemSaveRequestDto;
import greencity.dto.user.UserVO;
import greencity.enums.ShoppingListItemStatus;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.CustomShoppingListItemService;
import greencity.service.UserService;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static greencity.ModelUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
//@ContextConfiguration
//@Import(SecurityConfig.class)
class CustomShoppingListItemControllerTest {

    private static final String CONTROLLER_URL = "custom/shopping-list-items";
    public static final Long USER_ID = 2L;
    public static final Long HABIT_ID = 1L;
    public static final Long ITEM_ID = 1L;

//    private static Validator validator;

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
//    private ErrorAttributes errorAttributes = new DefaultErrorAttributes();
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
//                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
//                .setValidator(new LocalValidatorFactoryBean())
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

    @ParameterizedTest
    @ValueSource(strings = {"active", "done", "disabled", "inprogress"})
    void updateItemStatus(String itemStatus) throws Exception {
        var expectedResult = getCustomShoppingListItemResponseDto();
        expectedResult.setStatus(ShoppingListItemStatus.valueOf(itemStatus.toUpperCase()));

        when(customShoppingListItemService.updateItemStatus(anyLong(), anyLong(), anyString()))
                .thenReturn(expectedResult);

        mockMvc.perform(patch("/{url}/{userId}/custom-shopping-list-items", CONTROLLER_URL, USER_ID)
                        .param("itemId", String.valueOf(ITEM_ID))
                        .param("status", itemStatus)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));

        verify(customShoppingListItemService).updateItemStatus(USER_ID, ITEM_ID, itemStatus);
    }

    @Test
    void updateItemStatusToDone() throws Exception {
        doNothing().when(customShoppingListItemService).updateItemStatusToDone(anyLong(), anyLong());

        mockMvc.perform(patch("/{url}/{userId}/done", CONTROLLER_URL, USER_ID)
                        .param("itemId", String.valueOf(ITEM_ID)))
                .andExpect(status().isOk());

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

    @ParameterizedTest
    @ValueSource(strings = {"active", "done", "disabled", "inprogress"})
    void getAllCustomShoppingItemsByStatus_withStatus(String itemStatus) throws Exception {
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
    void getAllCustomShoppingItemsByStatus_withoutStatus() throws Exception {
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

}
