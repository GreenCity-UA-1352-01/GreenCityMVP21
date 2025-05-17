package greencity.service;


import greencity.ModelUtils;
import greencity.constant.AppConstant;
import greencity.dto.PageableDto;
import greencity.dto.friend.SearchFriendDtoResponse;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.BadRequestException;
import greencity.mapping.SearchFriendDtoResponseMapper;
import greencity.projection.UserWithMutualFriendsProjection;
import greencity.repository.FriendRepo;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FriendServiceImplTest {

    private static final UserVO USER;
    private static final UserWithMutualFriendsProjection FRIEND_PROJECTION;
    private static final SearchFriendDtoResponse FRIEND_DTO;
    private static final String NAME;
    private static final String MODIFIED_NAME;
    private static final String MODIFIED_CITY;
    private static final Pageable PAGEABLE;
    private static final Pageable MODIFIED_PAGEABLE;

    static {
        USER = ModelUtils.getUserVO();
        FRIEND_PROJECTION = ModelUtils.getUserWithMutualFriendsProjection();
        FRIEND_DTO = ModelUtils.getSearchFriendDtoResponse();
        NAME = "name";
        MODIFIED_NAME = "%" + String.join("%", NAME.split("")) + "%";
        MODIFIED_CITY = null;
        PAGEABLE = PageRequest.of(0, 100, Sort.unsorted());
        MODIFIED_PAGEABLE = PageRequest.of(0, AppConstant.FRIENDS_RESPONSE_SIZE, Sort.unsorted());
    }

    @Mock
    private FriendRepo friendRepo;
    @Mock
    private NotificationService notificationService;
    @Mock
    private NotificationWebSocketService notificationWebSocketService;
    @Spy
    private SearchFriendDtoResponseMapper mapper;
    @InjectMocks
    private FriendServiceImpl friendService;

    @BeforeEach
    void setUp() {
        reset(mapper);
    }

    @Test
    void testSearchNewFriends_whenNotFriendsOfFriends() {
        var actualPage = new PageImpl<>(List.of(FRIEND_PROJECTION), PAGEABLE, 1);
        var expected = new PageableDto<>(List.of(FRIEND_DTO), 1, 0, 1);

        when(friendRepo.findNotFriendsYet(USER.getId(), MODIFIED_NAME, MODIFIED_CITY, MODIFIED_PAGEABLE))
            .thenReturn(actualPage);

        var actual = assertDoesNotThrow(() -> friendService.searchNewFriends(NAME, false,
            false, USER, PAGEABLE));

        assertEquals(expected, actual);
        verify(friendRepo).findNotFriendsYet(USER.getId(), MODIFIED_NAME, MODIFIED_CITY, MODIFIED_PAGEABLE);
        verify(friendRepo, never()).findFriendsOfFriends(USER.getId(), MODIFIED_NAME, MODIFIED_CITY, MODIFIED_PAGEABLE);
    }

    @Test
    void testSearchNewFriends_whenFriendsOfFriends() {
        var actualPage = new PageImpl<>(List.of(FRIEND_PROJECTION), PAGEABLE, 1);
        var expected = new PageableDto<>(List.of(FRIEND_DTO), 1, 0, 1);

        when(friendRepo.findFriendsOfFriends(USER.getId(), MODIFIED_NAME, MODIFIED_CITY, MODIFIED_PAGEABLE))
            .thenReturn(actualPage);

        var actual = assertDoesNotThrow(() -> friendService.searchNewFriends(NAME, false,
            true, USER, PAGEABLE));

        assertEquals(expected, actual);
        verify(friendRepo).findFriendsOfFriends(USER.getId(), MODIFIED_NAME, MODIFIED_CITY, MODIFIED_PAGEABLE);
        verify(friendRepo, never()).findNotFriendsYet(USER.getId(), MODIFIED_NAME, MODIFIED_CITY, MODIFIED_PAGEABLE);
    }

    @Test
    void testSearchNewFriends_whenNullName_shouldThrowException() {
        assertThrows(BadRequestException.class, () -> friendService.searchNewFriends(null,
            false, false, USER, PAGEABLE));

        verify(friendRepo, never()).findNotFriendsYet(USER.getId(), null, MODIFIED_CITY, MODIFIED_PAGEABLE);
        verify(friendRepo, never()).findFriendsOfFriends(USER.getId(), null, MODIFIED_CITY, MODIFIED_PAGEABLE);
    }

    @Test
    void testSearchNewFriends_shouldCreatePatternFromName() {
        String expected = "%" + String.join("%", NAME.split("")) + "%";
        ArgumentCaptor<String> nameArgumentCaptor = ArgumentCaptor.forClass(String.class);

        when(friendRepo.findNotFriendsYet(USER.getId(), MODIFIED_NAME, USER.getCity(), MODIFIED_PAGEABLE))
            .thenReturn(new PageImpl<>(List.of(FRIEND_PROJECTION), PAGEABLE, 1));

        friendService.searchNewFriends(NAME, false, false, USER, PAGEABLE);

        verify(friendRepo).findNotFriendsYet(eq(USER.getId()), nameArgumentCaptor.capture(), eq(MODIFIED_CITY),
            eq(MODIFIED_PAGEABLE));
        assertEquals(expected, nameArgumentCaptor.getValue());
    }

    @Test
    void testSearchNewFriends_shouldSetCityIfTheSameCity() {
        ArgumentCaptor<String> cityArgumentCaptor = ArgumentCaptor.forClass(String.class);

        when(friendRepo.findNotFriendsYet(USER.getId(), MODIFIED_NAME, USER.getCity(), MODIFIED_PAGEABLE))
            .thenReturn(new PageImpl<>(List.of(FRIEND_PROJECTION), PAGEABLE, 1));

        friendService.searchNewFriends(NAME, true, false, USER, PAGEABLE);

        verify(friendRepo).findNotFriendsYet(eq(USER.getId()), eq(MODIFIED_NAME), cityArgumentCaptor.capture(),
            eq(MODIFIED_PAGEABLE));
        assertEquals(USER.getCity(), cityArgumentCaptor.getValue());
    }

    @Test
    void testSearchNewFriends_shouldSetCityToNullIfNotTheSameCity() {
        ArgumentCaptor<String> cityArgumentCaptor = ArgumentCaptor.forClass(String.class);

        when(friendRepo.findNotFriendsYet(USER.getId(), MODIFIED_NAME, USER.getCity(), MODIFIED_PAGEABLE))
            .thenReturn(new PageImpl<>(List.of(FRIEND_PROJECTION), PAGEABLE, 1));

        friendService.searchNewFriends(NAME, false, false, USER, PAGEABLE);

        verify(friendRepo).findNotFriendsYet(eq(USER.getId()), eq(MODIFIED_NAME), cityArgumentCaptor.capture(),
            eq(MODIFIED_PAGEABLE));
        assertNull(cityArgumentCaptor.getValue());
    }

    @Test
    void testSearchNewFriends_shouldSetPageSizeToConstant() {
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(friendRepo.findNotFriendsYet(USER.getId(), MODIFIED_NAME, USER.getCity(), MODIFIED_PAGEABLE))
            .thenReturn(new PageImpl<>(List.of(FRIEND_PROJECTION), PAGEABLE, 1));

        friendService.searchNewFriends(NAME, false, false, USER, PAGEABLE);

        verify(friendRepo).findNotFriendsYet(eq(USER.getId()), eq(MODIFIED_NAME), isNull(),
            pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertEquals(AppConstant.FRIENDS_RESPONSE_SIZE, pageable.getPageSize());
    }

    @Test
    void testAddFriend() {
        assertDoesNotThrow(() -> friendService.addFriend(1L, 2L));

        verify(friendRepo).addFriend(1L, 2L);
    }

    @Test
    void testAddFriend_whenUserIdNull_shouldThrowException() {
        assertThrows(BadRequestException.class, () -> friendService.addFriend(null, 2L));

        verify(friendRepo, never()).addFriend(isNull(), anyLong());
    }

    @Test
    void testAddFriend_whenFriendIdNull_shouldThrowException() {
        assertThrows(BadRequestException.class, () -> friendService.addFriend(1L, null));

        verify(friendRepo, never()).addFriend(anyLong(), isNull());
    }

    @Test
    void testAddFriend_whenUserIdEqualsFriendId_shouldDoNothing() {
        Long sameId = 1L;
        assertDoesNotThrow(() -> friendService.addFriend(sameId, sameId));

        verify(friendRepo, never()).addFriend(sameId, sameId);
    }
}