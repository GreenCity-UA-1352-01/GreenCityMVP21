package greencity.service;

import greencity.dto.notification.NotificationRequestDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.enums.NotificationObjectType;
import greencity.enums.NotificationType;
import greencity.enums.NotificationOrigin;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserTagServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UserTagServiceImpl userTagService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = new User();
        user1.setId(1L);
        user1.setName("John_Doe");
        user1.setEmail("john@example.com");
        user1.setRole(Role.ROLE_USER);
        user1.setUserStatus(UserStatus.ACTIVATED);

        user2 = new User();
        user2.setId(2L);
        user2.setName("Jane_Smith");
        user2.setEmail("jane@example.com");
        user2.setRole(Role.ROLE_USER);
        user2.setUserStatus(UserStatus.ACTIVATED);
    }

    @Test
    void findUsersByName_ReturnsMatchingUsers() {
        when(userRepo.findByNameContainingIgnoreCase("John")).thenReturn(Collections.singletonList(user1));

        List<UserVO> result = userTagService.findUsersByName("John");

        assertEquals(1, result.size());
        assertEquals(user1.getId(), result.getFirst().getId());
        assertEquals(user1.getName(), result.getFirst().getName());
        assertEquals(user1.getEmail(), result.getFirst().getEmail());
        verify(userRepo, times(1)).findByNameContainingIgnoreCase("John");
    }

    @Test
    void processUserMentions_WithValidMentions_CreatesNotifications() {
        String commentText = "Hello @John_Doe and @Jane_Smith, check this out!";
        Long initiatorId = 3L;
        String objectType = "event";
        Long objectId = 123L;
        Long commentId = 456L;

        when(userRepo.findByNameContainingIgnoreCase("John_Doe")).thenReturn(Collections.singletonList(user1));
        when(userRepo.findByNameContainingIgnoreCase("Jane_Smith")).thenReturn(Collections.singletonList(user2));
        when(notificationService.createNotifications(any())).thenReturn(Collections.emptyList());

        userTagService.processUserMentions(commentText, initiatorId, objectType, objectId, commentId);

        ArgumentCaptor<NotificationRequestDto> captor = ArgumentCaptor.forClass(NotificationRequestDto.class);
        verify(notificationService, times(1)).createNotifications(captor.capture());

        NotificationRequestDto capturedDto = captor.getValue();
        assertEquals(NotificationType.USER_MENTION, capturedDto.getNotificationType());
        assertEquals(initiatorId, capturedDto.getInitiatorId());
        assertEquals(objectId, capturedDto.getObjectId());
        assertEquals(NotificationObjectType.EVENT, capturedDto.getObjectType());
        assertEquals(commentId, capturedDto.getCommentId());
        
        Set<Long> expectedReceiverIds = new HashSet<>(Arrays.asList(1L, 2L));
        assertEquals(expectedReceiverIds, capturedDto.getReceiverIds());
    }

    @Test
    void processUserMentions_WithNoMentions_DoesNotCreateNotifications() {
        String commentText = "Hello everyone, check this out!";
        Long initiatorId = 3L;
        String objectType = "event";
        Long objectId = 123L;
        Long commentId = 456L;

        userTagService.processUserMentions(commentText, initiatorId, objectType, objectId, commentId);

        verify(userRepo, never()).findByNameContainingIgnoreCase(anyString());
        verify(notificationService, never()).createNotifications(any());
    }

    @Test
    void processUserMentions_WithNullText_DoesNotCreateNotifications() {
        String commentText = null;
        Long initiatorId = 3L;
        String objectType = "event";
        Long objectId = 123L;
        Long commentId = 456L;

        userTagService.processUserMentions(commentText, initiatorId, objectType, objectId, commentId);

        verify(userRepo, never()).findByNameContainingIgnoreCase(anyString());
        verify(notificationService, never()).createNotifications(any());
    }
}