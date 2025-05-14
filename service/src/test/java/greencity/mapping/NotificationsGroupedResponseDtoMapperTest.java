package greencity.mapping;

import static org.junit.jupiter.api.Assertions.*;
import greencity.ModelUtils;
import greencity.dto.notification.NotificationResponseDto;
import greencity.dto.notification.NotificationsGroupedResponseDto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationsGroupedResponseDtoMapperTest {

    private static final NotificationResponseDto NOTIFICATION_RESPONSE_DTO;
    private static final NotificationsGroupedResponseDto GROUPED_RESPONSE_DTO;

    static {
        NOTIFICATION_RESPONSE_DTO = ModelUtils.getNotificationResponseDto();
        GROUPED_RESPONSE_DTO = ModelUtils.getNotificationsGroupedResponseDto();
    }

    private NotificationsGroupedResponseDtoMapper notificationMapper;

    @BeforeEach
    void setUp() {
        notificationMapper = new NotificationsGroupedResponseDtoMapper();
    }

    @Test
    void testConvert() {
        NotificationsGroupedResponseDto actual = notificationMapper.convert(List.of(NOTIFICATION_RESPONSE_DTO));

        assertEquals(GROUPED_RESPONSE_DTO, actual);
    }
}