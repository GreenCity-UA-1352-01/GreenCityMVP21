package greencity.service;

import greencity.dto.notification.NotificationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NotificationWebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendFriendRequestNotification(Long recipientId, NotificationRequestDto requestDto) {
        messagingTemplate.convertAndSend("/topic/notifications/" + recipientId, requestDto);
    }
}
