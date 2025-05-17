package greencity;

import greencity.constant.AppConstant;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.econews.*;
import greencity.dto.econewscomment.*;
import greencity.dto.event.*;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.dto.eventdatetime.EventDateTimeLocationRequestDto;
import greencity.dto.habit.*;
import greencity.dto.habitfact.*;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.language.LanguageVO;
import greencity.dto.notification.NotificationDateTimeFormatter;
import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.dto.notification.NotificationsGroupedResponseDto;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemVO;
import greencity.dto.tag.*;
import greencity.dto.user.*;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.entity.*;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.entity.localization.TagTranslation;
import greencity.enums.*;
import java.util.stream.Collectors;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static greencity.enums.UserStatus.ACTIVATED;

public class ModelUtils {
    public static User TEST_USER = createUser();
    public static User TEST_USER_ROLE_USER = createUserRoleUser();
    public static UserVO TEST_USER_VO = createUserVO();
    public static UserVO TEST_USER_VO_ROLE_USER = createUserVORoleUser();
    public static UserStatusDto TEST_USER_STATUS_DTO = createUserStatusDto();
    public static String TEST_EMAIL = "test@mail.com";
    public static String TEST_EMAIL_2 = "test2@mail.com";
    public static ZonedDateTime zonedDateTime = ZonedDateTime.now();
    public static LocalDateTime localDateTime = LocalDateTime.now();
    public static ZonedDateTime FIXED_EVENT_START = ZonedDateTime.of(2025, 12, 12, 22, 0, 0, 0, ZoneOffset.UTC);
    public static ZonedDateTime FIXED_EVENT_END = ZonedDateTime.of(2025, 12, 13, 22, 0, 0, 0, ZoneOffset.UTC);
    public static ZonedDateTime UPDATE_EVENT_START = ZonedDateTime.of(2026, 12, 14, 10, 30, 0, 0, ZoneOffset.UTC);
    public static ZonedDateTime UPDATE_EVENT_END = ZonedDateTime.of(2026, 12, 15, 12, 28, 0, 0, ZoneOffset.UTC);


    public static Tag getTag() {
        return new Tag(1L, TagType.ECO_NEWS, getTagTranslations(), Collections.emptyList(), Collections.emptySet());
    }

    public static Tag getEventTag() {
        return new Tag(1L, TagType.EVENT, getEventTagTranslations(), Collections.emptyList(), Collections.emptySet());
    }

    public static TagVO getEventTagVO() {
        return new TagVO(1L, TagType.EVENT, getEventTagTranslationsVO(), Collections.emptyList(),
            Collections.emptySet());
    }

    public static Tag getHabitTag() {
        return new Tag(1L, TagType.HABIT, getHabitTagTranslations(), Collections.emptyList(),
            Collections.emptySet());
    }

    public static List<TagTranslation> getTagTranslations() {
        return Arrays.asList(
            TagTranslation.builder().id(1L).name("Новини").language(Language.builder().id(2L).code("ua").build())
                .build(),
            TagTranslation.builder().id(2L).name("News").language(Language.builder().id(1L).code("en").build())
                .build());
    }

    public static List<TagTranslation> getHabitTagTranslations() {
        return Arrays.asList(
            TagTranslation.builder().id(1L).name("Багаторазове використання")
                .language(Language.builder().id(2L).code("ua").build())
                .build(),
            TagTranslation.builder().id(2L).name("Reusable").language(Language.builder().id(1L).code("en").build())
                .build());
    }

    public static List<TagTranslation> getEventTagTranslations() {
        Language language = getLanguage();
        return Arrays.asList(
            TagTranslation.builder().id(1L).name("Соціальний").language(getLanguageUa()).build(),
            TagTranslation.builder().id(2L).name("Social").language(language).build(),
            TagTranslation.builder().id(3L).name("Соціальний").language(language).build());
    }

    public static List<TagTranslationVO> getEventTagTranslationsVO() {
        return Arrays.asList(
            TagTranslationVO.builder().id(1L).name("Соціальний").build(),
            TagTranslationVO.builder().id(2L).name("Social").build(),
            TagTranslationVO.builder().id(3L).name("Соціальний").build());
    }

    public static TagDto getTagDto() {
        return TagDto.builder().id(2L).name("News").build();
    }

    public static List<Tag> getTags() {
        return Collections.singletonList(getTag());
    }

    public static List<Tag> getHabitsTags() {
        return Collections.singletonList(getHabitTag());
    }

    public static User getUser() {
        return User.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .userStatus(UserStatus.ACTIVATED)
            .lastActivityTime(localDateTime)
            .verifyEmail(new VerifyEmail())
            .dateOfRegistration(localDateTime)
            .build();
    }

    public static UserVO getUserVO() {
        return UserVO.builder()
            .id(1L)
            .email(TestConst.EMAIL)
            .name(TestConst.NAME)
            .role(Role.ROLE_USER)
            .lastActivityTime(localDateTime)
            .verifyEmail(new VerifyEmailVO())
            .dateOfRegistration(localDateTime)
            .build();
    }

    public static UserManagementVO getUserManagementVO() {
        return UserManagementVO.builder()
            .id(1L)
            .userStatus(ACTIVATED)
            .email("Test@gmail.com")
            .role(Role.ROLE_ADMIN).build();
    }

    public static UserVO getUserVOWithData() {
        return UserVO.builder()
            .id(13L)
            .name("user")
            .email("namesurname1995@gmail.com")
            .role(Role.ROLE_USER)
            .userCredo("save the world")
            .firstName("name")
            .emailNotification(EmailNotification.MONTHLY)
            .userStatus(UserStatus.ACTIVATED)
            .rating(13.4)
            .verifyEmail(VerifyEmailVO.builder()
                .id(32L)
                .user(UserVO.builder()
                    .id(13L)
                    .name("user")
                    .build())
                .expiryDate(LocalDateTime.of(2021, 7, 7, 7, 7))
                .token("toooookkkeeeeen42324532542")
                .build())
            .userFriends(Collections.singletonList(
                UserVO.builder()
                    .id(75L)
                    .name("Andrew")
                    .build()))
            .refreshTokenKey("refreshtoooookkkeeeeen42324532542")
            .ownSecurity(null)
            .dateOfRegistration(LocalDateTime.of(2020, 6, 6, 13, 47))
            .city("Lviv")
            .showShoppingList(true)
            .showEcoPlace(true)
            .showLocation(true)
            .ownSecurity(OwnSecurityVO.builder()
                .id(1L)
                .password("password")
                .user(UserVO.builder()
                    .id(13L)
                    .build())
                .build())
            .lastActivityTime(LocalDateTime.of(2020, 12, 11, 13, 30))
            .build();
    }

    public static Language getLanguage() {
        return new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList());
    }

    public static Language getLanguageUa() {
        return new Language(2L, "ua", Collections.emptyList(), Collections.emptyList());
    }

    public static EcoNews getEcoNews() {
        Tag tag = new Tag();
        tag.setTagTranslations(
            List.of(TagTranslation.builder().name("Новини").language(Language.builder().code("ua").build()).build(),
                TagTranslation.builder().name("News").language(Language.builder().code("en").build()).build()));
        return new EcoNews(1L, zonedDateTime, TestConst.SITE, "source", "shortInfo", getUser(),
            "title", "text", List.of(EcoNewsComment.builder().id(1L).text("test").build()),
            Collections.singletonList(tag), Collections.emptySet(), Collections.emptySet());
    }

    public static EcoNews getEcoNewsForFindDtoByIdAndLanguage() {
        return new EcoNews(1L, null, TestConst.SITE, null, "shortInfo", getUser(),
            "title", "text", null, Collections.singletonList(getTag()), Collections.emptySet(), Collections.emptySet());
    }

    public static EcoNewsVO getEcoNewsVO() {
        return new EcoNewsVO(1L, zonedDateTime, TestConst.SITE, null, getUserVO(),
            "title", "text", null, Collections.emptySet(), Collections.singletonList(getTagVO()),
            Collections.emptySet());
    }

    public static HabitStatusCalendar getHabitStatusCalendar() {
        return HabitStatusCalendar.builder()
            .enrollDate(LocalDate.now()).id(1L).build();
    }

    public static HabitAssign getHabitAssign() {
        return HabitAssign.builder()
            .id(1L)
            .status(HabitAssignStatus.ACQUIRED)
            .createDate(ZonedDateTime.now())
            .habit(Habit.builder()
                .id(1L)
                .image("")
                .habitTranslations(Collections.singletonList(HabitTranslation.builder()
                    .id(1L)
                    .name("")
                    .description("")
                    .habitItem("")
                    .language(getLanguage())
                    .build()))
                .build())
            .user(getUser())
            .userShoppingListItems(new ArrayList<>())
            .workingDays(0)
            .duration(0)
            .habitStreak(0)
            .habitStatistic(Collections.singletonList(getHabitStatistic()))
            .habitStatusCalendars(Collections.singletonList(getHabitStatusCalendar()))
            .lastEnrollmentDate(ZonedDateTime.now())
            .build();
    }

    public static HabitStatistic getHabitStatistic() {
        return HabitStatistic.builder()
            .id(1L).habitRate(HabitRate.GOOD).createDate(ZonedDateTime.now())
            .amountOfItems(10).build();
    }

    public static UserShoppingListItem getCustomUserShoppingListItem() {
        return UserShoppingListItem.builder()
            .id(1L)
            .habitAssign(HabitAssign.builder().id(1L).build())
            .status(ShoppingListItemStatus.DONE)
            .build();
    }

    public static UserShoppingListItem getFullUserShoppingListItem() {
        return UserShoppingListItem.builder()
            .id(1L)
            .shoppingListItem(getShoppingListItem())
            .habitAssign(HabitAssign.builder().id(1L).build())
            .status(ShoppingListItemStatus.DONE)
            .build();
    }

    public static UserShoppingListItemResponseDto getUserShoppingListItemResponseDto() {
        return UserShoppingListItemResponseDto.builder()
            .id(1L)
            .text("Buy electric car")
            .status(ShoppingListItemStatus.ACTIVE)
            .build();
    }

    public static UserShoppingListItem getPredefinedUserShoppingListItem() {
        return UserShoppingListItem.builder()
            .id(2L)
            .habitAssign(HabitAssign.builder().id(1L).build())
            .status(ShoppingListItemStatus.ACTIVE)
            .shoppingListItem(ShoppingListItem.builder().id(1L).userShoppingListItems(Collections.emptyList())
                .translations(
                    getShoppingListItemTranslations())
                .build())
            .build();
    }

    public static UserShoppingListItemVO getUserShoppingListItemVO() {
        return UserShoppingListItemVO.builder()
            .id(1L)
            .habitAssign(HabitAssignVO.builder()
                .id(1L)
                .build())
            .status(ShoppingListItemStatus.DONE)
            .build();
    }

    public static UserShoppingListItem getUserShoppingListItem() {
        return UserShoppingListItem.builder()
            .id(1L)
            .status(ShoppingListItemStatus.DONE)
            .habitAssign(HabitAssign.builder()
                .id(1L)
                .status(HabitAssignStatus.ACQUIRED)
                .habitStreak(10)
                .duration(300)
                .lastEnrollmentDate(ZonedDateTime.now())
                .workingDays(5)
                .build())
            .shoppingListItem(ShoppingListItem.builder()
                .id(1L)
                .build())
            .dateCompleted(LocalDateTime.of(2021, 2, 2, 14, 2))
            .build();
    }

    public static List<ShoppingListItemTranslation> getShoppingListItemTranslations() {
        return Arrays.asList(
            ShoppingListItemTranslation.builder()
                .id(2L)
                .language(new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(),
                    Collections.emptyList()))
                .content("Buy a bamboo toothbrush")
                .shoppingListItem(
                    new ShoppingListItem(1L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
                .build(),
            ShoppingListItemTranslation.builder()
                .id(11L)
                .language(new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(),
                    Collections.emptyList()))
                .content("Start recycling batteries")
                .shoppingListItem(
                    new ShoppingListItem(4L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
                .build());
    }

    public static HabitFactTranslation getFactTranslation() {
        return HabitFactTranslation.builder()
            .id(1L)
            .factOfDayStatus(FactOfDayStatus.CURRENT)
            .habitFact(null)
            .content("Content")
            .language(getLanguage())
            .build();
    }

    public static HabitFactTranslationVO getFactTranslationVO() {
        return HabitFactTranslationVO.builder()
            .id(1L)
            .factOfDayStatus(FactOfDayStatus.CURRENT)
            .habitFact(null)
            .language(getLanguageVO())
            .content("Content")
            .build();
    }

    public static LanguageTranslationDTO getLanguageTranslationDTO() {
        return new LanguageTranslationDTO(getLanguageDTO(), "content");
    }

    public static LanguageDTO getLanguageDTO() {
        return new LanguageDTO(1L, "en");
    }

    public static AddEcoNewsDtoRequest getAddEcoNewsDtoRequest() {
        return new AddEcoNewsDtoRequest("title", "text",
            Collections.singletonList("News"), "source");
    }

    public static AddEcoNewsDtoResponse getAddEcoNewsDtoResponse() {
        return new AddEcoNewsDtoResponse(1L, "title",
            "text", "shortInfo", EcoNewsAuthorDto.builder().id(1L).name(TestConst.NAME).build(),
            ZonedDateTime.now(), TestConst.SITE, "source",
            Arrays.asList("Новини", "News"));
    }

    public static MultipartFile getFile() {
        Path path = Paths.get("src/test/resources/test.jpg");
        String name = TestConst.IMG_NAME;
        String contentType = "photo/plain";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return new MockMultipartFile(name,
            name, contentType, content);
    }

    public static URL getUrl() throws MalformedURLException {
        return new URL(TestConst.SITE);
    }

    public static EcoNewsAuthorDto getEcoNewsAuthorDto() {
        return new EcoNewsAuthorDto(1L, TestConst.NAME);
    }

    public static List<TagTranslationVO> getTagTranslationsVO() {
        return Arrays.asList(TagTranslationVO.builder().id(1L).name("Новини")
                .languageVO(LanguageVO.builder().id(1L).code("ua").build()).build(),
            TagTranslationVO.builder().id(2L).name("News").languageVO(LanguageVO.builder().id(2L).code("en").build())
                .build());
    }

    public static LanguageVO getLanguageVO() {
        return new LanguageVO(1L, AppConstant.DEFAULT_LANGUAGE_CODE);
    }

    public static TagVO getTagVO() {
        return new TagVO(1L, TagType.ECO_NEWS, getTagTranslationsVO(), null, null);
    }

    public static TagPostDto getTagPostDto() {
        return new TagPostDto(TagType.ECO_NEWS, getTagTranslationDtos());
    }

    public static List<TagTranslationDto> getTagTranslationDtos() {
        return Arrays.asList(
            TagTranslationDto.TagTranslationDtoBuilder().name("Новини")
                .language(LanguageDTO.builder().id(2L).code("ua").build()).build(),
            TagTranslationDto.TagTranslationDtoBuilder().name("News")
                .language(LanguageDTO.builder().id(1L).code("en").build()).build());
    }

    public static TagViewDto getTagViewDto() {
        return new TagViewDto("3", "ECO_NEWS", "News");
    }

    public static PageableAdvancedDto<TagVO> getPageableAdvancedDtoForTag() {
        return new PageableAdvancedDto<>(Collections.singletonList(getTagVO()),
            9, 1, 2, 1,
            true, false, false, true);
    }

    public static AddEcoNewsCommentDtoResponse getAddEcoNewsCommentDtoResponse() {
        return AddEcoNewsCommentDtoResponse.builder()
            .id(getEcoNewsComment().getId())
            .author(getEcoNewsCommentAuthorDto())
            .text(getEcoNewsComment().getText())
            .modifiedDate(getEcoNewsComment().getModifiedDate())
            .build();
    }

    public static EcoNewsComment getEcoNewsComment() {
        return EcoNewsComment.builder()
            .id(1L)
            .text("text")
            .createdDate(LocalDateTime.now())
            .modifiedDate(LocalDateTime.now())
            .user(getUser())
            .ecoNews(getEcoNews())
            .build();
    }

    public static EcoNewsCommentAuthorDto getEcoNewsCommentAuthorDto() {
        return EcoNewsCommentAuthorDto.builder()
            .id(getUser().getId())
            .name(getUser().getName().trim())
            .userProfilePicturePath(getUser().getProfilePicturePath())
            .build();
    }

    public static AddEcoNewsCommentDtoRequest getAddEcoNewsCommentDtoRequest() {
        return new AddEcoNewsCommentDtoRequest("text", 0L);
    }

    public static EcoNewsCommentDto getEcoNewsCommentDto() {
        return EcoNewsCommentDto.builder()
            .id(1L)
            .modifiedDate(LocalDateTime.now())
            .author(getEcoNewsCommentAuthorDto())
            .text("text")
            .replies(0)
            .likes(0)
            .currentUserLiked(false)
            .status(CommentStatus.ORIGINAL)
            .build();
    }

    public static List<LanguageTranslationDTO> getLanguageTranslationsDTOs() {
        return Arrays.asList(
            new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "hello"),
            new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "text"),
            new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "smile"));
    }

    public static EcoNewsDto getEcoNewsDto() {
        return new EcoNewsDto(ZonedDateTime.now(), "imagePath", 1L, "title", "content", "text",
            getEcoNewsAuthorDto(), Collections.singletonList("tag"), Collections.singletonList("тег"), 1, 0, 0);
    }

    public static EcoNewsGenericDto getEcoNewsGenericDto() {
        String[] tagsEn = {"News"};
        String[] tagsUa = {"Новини"};
        return new EcoNewsGenericDto(1L, "title", "text", "shortInfo",
            ModelUtils.getEcoNewsAuthorDto(), zonedDateTime, "https://google.com/", "source",
            List.of(tagsUa), List.of(tagsEn), 0, 1, 0);
    }

    public static EcoNewsDto getEcoNewsDtoForFindDtoByIdAndLanguage() {
        return new EcoNewsDto(null, TestConst.SITE, 1L, "title", "text", "shortInfo",
            getEcoNewsAuthorDto(), Collections.singletonList("News"), Collections.singletonList("Новини"), 0, 0, 0);
    }

    public static UpdateEcoNewsDto getUpdateEcoNewsDto() {
        return new UpdateEcoNewsDto(1L, "title", "text", "shortInfo", Collections.singletonList("tag"),
            "image", "source");
    }

    public static SearchNewsDto getSearchNewsDto() {
        return new SearchNewsDto(1L, "title", getEcoNewsAuthorDto(), ZonedDateTime.now(),
            Collections.singletonList("tag"));
    }

    public static EcoNewsCommentVO getEcoNewsCommentVO() {
        return new EcoNewsCommentVO(1L, "text", LocalDateTime.now(), LocalDateTime.now(), new EcoNewsCommentVO(),
            new ArrayList<>(), getUserVO(), getEcoNewsVO(), false,
            false, new HashSet<>());
    }

    public static EcoNewsDtoManagement getEcoNewsDtoManagement() {
        return new EcoNewsDtoManagement(1L, "title", "text", ZonedDateTime.now(),
            Collections.singletonList("tag"), "imagePath", "source");
    }

    public static EcoNewsViewDto getEcoNewsViewDto() {
        return new EcoNewsViewDto("1", "title", "author", "text", "startDate",
            "endDate", "tag");
    }

    public static ShoppingListItem getShoppingListItem() {
        return ShoppingListItem.builder()
            .id(1L)
            .translations(getShoppingListItemTranslations())
            .build();
    }

    public static HabitAssignPropertiesDto getHabitAssignPropertiesDto() {
        return HabitAssignPropertiesDto.builder()
            .defaultShoppingListItems(List.of(1L))
            .duration(20)
            .build();
    }

    public static HabitAssign getHabitAssignWithUserShoppingListItem() {
        return HabitAssign.builder()
            .id(1L)
            .user(User.builder().id(21L).build())
            .habit(Habit.builder().id(1L).build())
            .status(HabitAssignStatus.INPROGRESS)
            .workingDays(0)
            .duration(20)
            .userShoppingListItems(List.of(UserShoppingListItem.builder()
                .id(1L)
                .shoppingListItem(ShoppingListItem.builder().id(1L).build())
                .status(ShoppingListItemStatus.INPROGRESS)
                .build()))
            .build();
    }

    private static UserStatusDto createUserStatusDto() {
        return UserStatusDto.builder()
            .id(2L)
            .userStatus(UserStatus.CREATED)
            .build();
    }

    private static User createUserRoleUser() {
        return User.builder()
            .id(2L)
            .role(Role.ROLE_USER)
            .email("test2@mail.com")
            .build();
    }

    private static UserVO createUserVORoleUser() {
        return UserVO.builder()
            .id(2L)
            .role(Role.ROLE_USER)
            .email("test2@mail.com")
            .build();
    }

    private static User createUser() {
        return User.builder()
            .id(1L)
            .role(Role.ROLE_MODERATOR)
            .email("test@mail.com")
            .build();
    }

    private static UserVO createUserVO() {
        return UserVO.builder()
            .id(1L)
            .role(Role.ROLE_MODERATOR)
            .email("test@mail.com")
            .build();
    }

    public static List<UserShoppingListItemVO> getUserShoppingListItemVOList() {
        List<UserShoppingListItemVO> list = new ArrayList<>();
        list.add(UserShoppingListItemVO.builder()
            .id(1L)
            .build());
        return list;
    }

    public static List<CustomShoppingListItemVO> getCustomShoppingListItemVOList() {
        List<CustomShoppingListItemVO> list = new ArrayList<>();
        list.add(CustomShoppingListItemVO.builder()
            .id(1L)
            .text("text")
            .build());
        return list;
    }

    public static CustomShoppingListItemResponseDto getCustomShoppingListItemResponseDto() {
        return CustomShoppingListItemResponseDto.builder()
            .id(1L)
            .status(ShoppingListItemStatus.INPROGRESS)
            .text("TEXT")
            .build();
    }

    public static CustomShoppingListItem getCustomShoppingListItem() {
        return CustomShoppingListItem.builder()
            .id(1L)
            .status(ShoppingListItemStatus.INPROGRESS)
            .text("TEXT")
            .build();
    }

    public static Principal getPrincipal() {
        return () -> "danylo@gmail.com";
    }

    public static UserFilterDtoRequest getUserFilterDtoRequest() {
        return UserFilterDtoRequest.builder()
            .userRole("USER")
            .name("Test_Filter")
            .searchCriteria("Test")
            .userStatus("ACTIVATED")
            .build();
    }

    public static UserFilterDtoResponse getUserFilterDtoResponse() {
        return UserFilterDtoResponse.builder()
            .id(1L)
            .userRole("ADMIN")
            .searchCriteria("Test")
            .userStatus("ACTIVATED")
            .name("Test")
            .build();
    }

    public static Filter getFilter() {
        return Filter.builder()
            .id(1L)
            .name("Test")
            .user(new User())
            .type("USERS")
            .values("Test;ADMIN;ACTIVATED")
            .build();
    }

    public static CustomShoppingListItem getCustomShoppingListItemWithStatusInProgress() {
        return CustomShoppingListItem.builder()
            .id(2L)
            .habit(Habit.builder()
                .id(3L)
                .build())
            .user(getUser())
            .text("item")
            .status(ShoppingListItemStatus.INPROGRESS)
            .build();
    }

    public static CustomShoppingListItemResponseDto getCustomShoppingListItemResponseDtoWithStatusInProgress() {
        return CustomShoppingListItemResponseDto.builder()
            .id(2L)
            .text("item")
            .status(ShoppingListItemStatus.INPROGRESS)
            .build();
    }

    public static CreateEventDto getCreateEventDto() {
        return CreateEventDto.builder()
            .title("title")
            .description("description")
            .dates(List.of(getEventDateLocationDto()))
            .tags(List.of(getEventTag().getType().toString()))
            .mainImage("mainImage")
            .open(true)
            .online(false)
            .initiativeTypes(List.of("INITIATIVE_TYPE"))
            .build();
    }

    public static CreateEventDtoResponse getCreateEventDtoResponse() {
        Tag tag = getEventTag();
        List<String> tagTranslations = tag.getTagTranslations().stream()
            .map(TagTranslation::getName)
            .collect(Collectors.toList());

        return CreateEventDtoResponse.builder()
            .eventId(1L)
            .title("title")
            .description("description")
            .open(true)
            .tags(tagTranslations)
            .dates(List.of(getEventDateLocationDto()))
            .images(List.of("mainImage"))
            .createdDateTime(ZonedDateTime.now())
            .build();
    }


    public static EventDateLocationDto getEventDateLocationDto() {
        return EventDateLocationDto.builder()
            .startDateTime(FIXED_EVENT_START)
            .endDateTime(FIXED_EVENT_END)
            .location("location")
            .build();
    }

    public static EventDateTimeLocationRequestDto getEventDateTimeLocationRequestDto() {
        return EventDateTimeLocationRequestDto.builder()
            .id(1L)
            .startDateTime(FIXED_EVENT_START)
            .endDateTime(FIXED_EVENT_END)
            .location("location")
            .build();
    }

    public static Event getEvent() {
        return Event.builder()
            .title("title")
            .description("description")
            .dateTimes(List.of(EventDateTimeLocation.builder()
                .startDateTime(FIXED_EVENT_START)
                .endDateTime(FIXED_EVENT_END)
                .location("location")
                .link("link")
                .build()))
            .mainImage(EventImage.builder()
                .id(1L)
                .imagePath("mainImage")
                .build())
            .initiator(getUser())
            .eventImages(new ArrayList<>(List.of(
                EventImage.builder().imagePath("https://cdn.com/file/main.jpg").build()
            )))
            .tags(Set.of(getEventTag()))
            .isOpen(true)
            .build();

    }

    public static EventDateTimeLocation getEventDateTimeLocation() {
        return EventDateTimeLocation.builder()
                .startDateTime(FIXED_EVENT_START)
                .endDateTime(FIXED_EVENT_END)
                .location("location")
                .build();

    }

    public static Event getEventWithoutImages() {
        Event event = getEventWithoutDates();
        List<EventDateTimeLocation> dateTimes = getEventDateTimeLocationsWithoutEvent();
        event.setDateTimes(dateTimes);
        dateTimes.forEach(dateTime -> dateTime.setEvent(event));
        return event;
    }

    private static Event getEventWithoutDates() {
        return Event.builder()
            .id(1L)
            .title("test")
            .description("test")
            .initiator(getUser())
            .tags(Set.copyOf(getTags()))
            .isOpen(true)
            .build();
    }

    private static List<EventDateTimeLocation> getEventDateTimeLocationsWithoutEvent() {
        return Arrays.asList(
            EventDateTimeLocation.builder()
                .id(1L)
                .startDateTime(ZonedDateTime.now().minusDays(1).minusHours(1))
                .endDateTime(ZonedDateTime.now().minusDays(1))
                .location("test")
                .build(),
            EventDateTimeLocation.builder()
                .id(2L)
                .startDateTime(ZonedDateTime.now().plusDays(1))
                .endDateTime(ZonedDateTime.now().plusDays(1).plusHours(1))
                .link("test")
                .build()
        );
    }

    public static UpdateEventDtoRequest getUpdateEventDtoRequest() {
        return UpdateEventDtoRequest.builder()
            .id(1L)
            .title("Update Title")
            .description("description with more than 20 characters")
            .dateTimes(List.of(EventDateTimeLocationRequestDto.builder()
                .id(1L)
                .startDateTime(UPDATE_EVENT_START)
                .endDateTime(UPDATE_EVENT_END)
                .location("Update location")
                .link("Update link")
                .build()))
            .mainImage("UpdateMain.jpg")
            .images(List.of(
                "https://csb10032000a548f571.blob.core.windows.net/allfiles/e04cc9f5-4fc5-438d-9dfe-48dc80a86704cute-cat-indoors.jpg"
            ))
            .tags(List.of("Соціальний"))
            .isOpen(true)
            .build();
    }

    public static NotificationReceiver getNotificationReceiver() {
        NotificationReceiver notificationReceiver = getNotificationReceiverWithoutNotification();
        Notification notification = getNotificationWithoutReceivers();
        notification.getNotificationReceivers().add(notificationReceiver);
        notificationReceiver.setNotification(notification);
        return notificationReceiver;
    }

    public static Notification getNotification() {
        NotificationReceiver notificationReceiver = getNotificationReceiverWithoutNotification();
        Notification notification = getNotificationWithoutReceivers();
        notification.getNotificationReceivers().add(notificationReceiver);
        notificationReceiver.setNotification(notification);
        return notification;
    }

    private static NotificationReceiver getNotificationReceiverWithoutNotification() {
        return NotificationReceiver.builder()
            .id(1L)
            .receiver(getUser())
            .status(NotificationStatus.UNREAD)
            .build();
    }

    private static Notification getNotificationWithoutReceivers() {
        return Notification.builder()
            .id(1L)
            .action("test")
            .objectId(1L)
            .objectName("test")
            .creationDate(zonedDateTime)
            .notificationType(NotificationType.EVENT_COMMENT)
            .objectType(NotificationObjectType.EVENT)
            .initiator(getUser().setId(1L))
            .origin(NotificationOrigin.GREEN_CITY)
            .build();
    }

    public static NotificationCounter getNotificationCounter() {
        return NotificationCounter.builder()
            .countOfNotifications(1)
            .user(getUser().setId(2L))
            .build();
    }

    public static NotificationRequestDto getNotificationRequestDto() {
        return NotificationRequestDto.builder()
            .initiatorId(1L)
            .action("test")
            .receiverIds(Set.of(2L))
            .objectId(1L)
            .objectName("test")
            .objectType(NotificationObjectType.EVENT)
            .creationDate(zonedDateTime)
            .notificationType(NotificationType.EVENT_COMMENT)
            .origin(NotificationOrigin.GREEN_CITY)
            .build();
    }

    public static NotificationResponseDto getNotificationResponseDto() {
        return NotificationResponseDto.builder()
            .id(1L)
            .initiatorId(1L)
            .initiatorName("Taras")
            .receiverId(1L)
            .objectId(1L)
            .objectName("test")
            .objectLink("/event/1")
            .objectType(NotificationObjectType.EVENT)
            .action("test")
            .creationDate(zonedDateTime)
            .status(NotificationStatus.UNREAD)
            .notificationType(NotificationType.EVENT_COMMENT)
            .origin(NotificationOrigin.GREEN_CITY)
            .build();
    }

    public static NotificationsGroupedResponseDto getNotificationsGroupedResponseDto() {
        String dateTime = NotificationDateTimeFormatter.format(zonedDateTime);
        return NotificationsGroupedResponseDto.builder()
            .ids(Set.of(1L))
            .action("Taras commented on your event test. %s".formatted(dateTime))
            .objectId(1L)
            .objectName("test")
            .objectLink("/event/1")
            .objectType(NotificationObjectType.EVENT)
            .creationDate(zonedDateTime)
            .status(NotificationStatus.UNREAD)
            .receiverId(1L)
            .initiatorIds(Set.of(1L))
            .initiatorNames(Set.of("Taras"))
            .notificationType(NotificationType.EVENT_COMMENT)
            .origin(NotificationOrigin.GREEN_CITY)
            .build();
    }

    public static EventComment getEventComment() {
        return EventComment.builder()
            .id(1L)
            .text("text")
            .createdDate(LocalDateTime.now())
            .modifiedDate(LocalDateTime.now())
            .user(getUser())
            .event(getEvent())
            .deleted(false)
            .build();
    }

    public static EventImageDto getEventImageDto() {
        return EventImageDto.builder()
            .imagePath("main.jpg")
            .isMainImage(true)
            .build();
    }

    public static EventVO getEventVO() {
        return EventVO.builder()
            .id(1L)
            .title("title")
            .description("description with more than 20 characters")
            .dateTimes(List.of(getEventDateLocationDto()))
            .mainImage(getEventImageDto())
            .eventImages(List.of(getEventImageDto()))
            .tags(Set.of(getTagVO()))
            .isOpen(true)
            .initiator(getUserVO())
            .build();
    }

    public static AddEventCommentDtoRequest getAddEventCommentDtoRequest() {
        return AddEventCommentDtoRequest.builder()
            .text("text")
            .parentCommentId(1L)
            .build();
    }

    public static EventCommentAuthorDto getEventCommentAuthorDto() {
        return EventCommentAuthorDto.builder()
            .id(1L)
            .name("Taras")
            .build();
    }

    public static AddEventCommentDtoResponse getAddEventCommentDtoResponse() {
        return AddEventCommentDtoResponse.builder()
            .id(1L)
            .author(getEventCommentAuthorDto())
            .text("text")
            .modifiedDate(LocalDateTime.now())
            .build();
    }
}
