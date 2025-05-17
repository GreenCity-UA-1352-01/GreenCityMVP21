package greencity.mapping;

import static org.junit.jupiter.api.Assertions.*;
import greencity.ModelUtils;
import greencity.dto.notification.NotificationResponseDto;
import greencity.entity.Notification;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationResponseDtoMapperTest {

    private static final Notification NOTIFICATION;
    private static final NotificationResponseDto NOTIFICATION_RESPONSE_DTO;

    static {
        NOTIFICATION = ModelUtils.getNotification();
        NOTIFICATION_RESPONSE_DTO = ModelUtils.getNotificationResponseDto();
    }

    private NotificationResponseDtoMapper notificationMapper;

    @BeforeEach
    void setUp() {
        notificationMapper = new NotificationResponseDtoMapper();
    }

    @Test
    void testConvert() {
        List<NotificationResponseDto> actual = notificationMapper.convert(NOTIFICATION);

        assertIterableEquals(List.of(NOTIFICATION_RESPONSE_DTO), actual);
    }
}