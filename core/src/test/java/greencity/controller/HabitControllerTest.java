package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.converters.UserArgumentResolver;
import greencity.dto.habit.*;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitService;
import greencity.service.TagsService;
import greencity.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HabitControllerTest {
    private static final String habitLink = "/habit";
    private MockMvc mockMvc;

    @InjectMocks
    private HabitController habitController;

    @Mock
    private HabitService habitService;

    @Mock
    private TagsService tagsService;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    private final Principal principal = getPrincipal();

    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(habitController)
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper)
                )
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, new ObjectMapper()))
                .build();
    }

    @Test
    void getHabitByIdTest() throws Exception {
        mockMvc.perform(get(habitLink + "/{id}", 1))
                .andExpect(status().isOk());

        verify(habitService).getByIdAndLanguageCode(1L, "en");
    }

    @Test
    void getAllTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        Pageable pageable = PageRequest.of(0, 20);

        mockMvc.perform(get(habitLink)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitService).getAllHabitsByLanguageCode(userVO, pageable, "en");
    }

    @Test
    void getShoppingListItemsTest() throws Exception {
        mockMvc.perform(get(habitLink + "/{id}/shopping-list", 1))
                .andExpect(status().isOk());

        verify(habitService).getShoppingListForHabit(1L, "en");
    }

    @Test
    @SneakyThrows
    void getAllByTagsAndLanguageCodeTest() {
        Pageable pageable = PageRequest.of(0, 20);
        List<String> tags = Collections.singletonList("tag");

        mockMvc.perform(get(habitLink + "/tags/search")
                        .param("tags", tags.getFirst()))
                .andExpect(status().isOk());

        verify(habitService).getAllByTagsAndLanguageCode(pageable, tags, "en");
    }

    @Test
    @SneakyThrows
    void getAllByDifferentParametersTest() {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        Pageable pageable = PageRequest.of(0, 20);
        List<String> tags = Collections.singletonList("tag");
        Boolean isCustom = true;
        List<Integer> complexities = Collections.singletonList(1);

        mockMvc.perform(get(habitLink + "/search")
                        .param("tags", tags.getFirst())
                        .param("isCustomHabit", isCustom.toString())
                        .param("complexities", complexities.getFirst().toString())
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitService).getAllByDifferentParameters(
                eq(userVO),
                eq(pageable),
                eq(Optional.of(tags)),
                eq(Optional.of(isCustom)),
                eq(Optional.of(complexities)),
                eq("en")
        );
    }

    @Test
    @SneakyThrows
    void getAllByDifferentParametersNoParamsTest() {
        mockMvc.perform(get(habitLink + "/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllHabitsTagsTest() throws Exception {
        mockMvc.perform(get(habitLink + "/tags"))
                .andExpect(status().isOk());

        verify(tagsService).findAllHabitsTags("en");
    }

    @Test
    void addCustomHabitTest() throws Exception {
        String json = """
                {
                "complexity": 1,
                "defaultDuration": 7,
                "habitTranslations": [{\
                  "name": "Custom habit",\
                  "description": "Custom habit description",\
                  "languageCode": "en"\
                }],
                "tagIds": [1],
                "customShoppingListItemDto": []
                }""";

        MockMultipartFile jsonFile =
                new MockMultipartFile("request", "", "application/json", json.getBytes());

        MockMultipartFile imageFile =
                new MockMultipartFile("image", "image.jpg", "image/jpeg", "test-image".getBytes());

        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        AddCustomHabitDtoRequest dtoRequest = new ObjectMapper().readValue(json, AddCustomHabitDtoRequest.class);

        mockMvc.perform(multipart(habitLink + "/custom")
                        .file(jsonFile)
                        .file(imageFile)
                        .principal(principal)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        verify(habitService).addCustomHabit(eq(dtoRequest), eq(imageFile), eq(principal.getName()));
    }

    @Test
    void addCustomHabitBadRequestTest() throws Exception {
        String invalidJson = "{}";
        MockMultipartFile jsonFile =
                new MockMultipartFile("request", "", "application/json", invalidJson.getBytes());

        mockMvc.perform(multipart(habitLink + "/custom")
                        .file(jsonFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFriendsAssignedToHabitProfilePicturesTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(habitLink + "/{habitId}/friends/profile-pictures", 1)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(habitService).getFriendsAssignedToHabitProfilePictures(1L, userVO.getId());
    }

    @Test
    void getHabitByIdInvalidIdTest() throws Exception {
        when(habitService.getByIdAndLanguageCode(-1L, "en"))
                .thenThrow(new BadRequestException("Invalid habit id"));

        mockMvc.perform(get(habitLink + "/{id}", -1))
                .andExpect(status().isBadRequest());

        verify(habitService).getByIdAndLanguageCode(-1L, "en");
    }

    @Test
    void getAllWithInvalidPageParamsTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        when(habitService.getAllHabitsByLanguageCode(
                eq(userVO),
                any(Pageable.class),
                eq("en")))
                .thenThrow(new BadRequestException("Invalid page parameters"));

        mockMvc.perform(get(habitLink)
                        .param("page", "-1")
                        .param("size", "0")
                        .principal(principal))
                .andExpect(status().isBadRequest());

        verify(habitService).getAllHabitsByLanguageCode(
                eq(userVO),
                any(Pageable.class),
                eq("en"));
    }

    @Test
    void getShoppingListItemsInvalidIdTest() throws Exception {
        when(habitService.getShoppingListForHabit(-1L, "en"))
                .thenThrow(new BadRequestException("Invalid habit id"));

        mockMvc.perform(get(habitLink + "/{id}/shopping-list", -1))
                .andExpect(status().isBadRequest());

        verify(habitService).getShoppingListForHabit(-1L, "en");
    }

    @Test
    void getAllByTagsAndLanguageCodeEmptyTagsTest() throws Exception {
        mockMvc.perform(get(habitLink + "/tags/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllByDifferentParametersInvalidComplexityTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        when(habitService.getAllByDifferentParameters(
                eq(userVO),
                any(Pageable.class),
                eq(Optional.empty()),
                eq(Optional.empty()),
                eq(Optional.of(List.of(-1))),
                eq("en")))
                .thenThrow(new BadRequestException("Invalid complexity value"));

        mockMvc.perform(get(habitLink + "/search")
                        .param("complexities", "-1")
                        .principal(principal))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addCustomHabitInvalidImageFormatTest() throws Exception {
        String json = """
        {
        "complexity": 1,
        "defaultDuration": 7,
        "habitTranslations": [{
          "name": "Custom habit",
          "description": "Custom habit description",
          "languageCode": "en"
        }],
        "tagIds": [1],
        "customShoppingListItemDto": []
        }""";

        MockMultipartFile jsonFile =
                new MockMultipartFile("request", "", "application/json", json.getBytes());

        MockMultipartFile invalidImageFile =
                new MockMultipartFile("image", "test.txt", "text/plain", "invalid-image".getBytes());

        when(habitService.addCustomHabit(any(), eq(invalidImageFile), anyString()))
                .thenThrow(new BadRequestException("Invalid image format"));

        mockMvc.perform(multipart(habitLink + "/custom")
                        .file(jsonFile)
                        .file(invalidImageFile)
                        .principal(principal)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addCustomHabitLargeImageTest() throws Exception {
        String json = """
        {
        "complexity": 1,
        "defaultDuration": 7,
        "habitTranslations": [{
          "name": "Custom habit",
          "description": "Custom habit description",
          "languageCode": "en"
        }],
        "tagIds": [1],
        "customShoppingListItemDto": []
        }""";

        MockMultipartFile jsonFile =
                new MockMultipartFile("request", "", "application/json", json.getBytes());

        byte[] largeImage = new byte[1024 * 1024 + 1];
        MockMultipartFile largeImageFile =
                new MockMultipartFile("image", "large.jpg", "image/jpeg", largeImage);

        when(habitService.addCustomHabit(any(), eq(largeImageFile), anyString()))
                .thenThrow(new BadRequestException("Image size exceeds 1MB"));

        mockMvc.perform(multipart(habitLink + "/custom")
                        .file(jsonFile)
                        .file(largeImageFile)
                        .principal(principal)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFriendsAssignedToHabitProfilePicturesInvalidHabitIdTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        when(habitService.getFriendsAssignedToHabitProfilePictures(-1L, userVO.getId()))
                .thenThrow(new BadRequestException("Invalid habit id"));

        mockMvc.perform(get(habitLink + "/{habitId}/friends/profile-pictures", -1)
                        .principal(principal))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addCustomHabitInvalidLanguageCodeTest() throws Exception {
        String json = """
        {
        "complexity": 1,
        "defaultDuration": 7,
        "habitTranslations": [{
          "name": "Custom habit",
          "description": "Custom habit description",
          "languageCode": "invalid"
        }],
        "tagIds": [1],
        "customShoppingListItemDto": []
        }""";

        MockMultipartFile jsonFile =
                new MockMultipartFile("request", "", "application/json", json.getBytes());
        MockMultipartFile imageFile =
                new MockMultipartFile("image", "image.jpg", "image/jpeg", "test-image".getBytes());

        when(habitService.addCustomHabit(any(), any(), anyString()))
                .thenThrow(new BadRequestException("Invalid language code"));

        mockMvc.perform(multipart(habitLink + "/custom")
                        .file(jsonFile)
                        .file(imageFile)
                        .principal(principal)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }
}