package greencity.controller;

import greencity.dto.notification.NotificationResponseDto;
import greencity.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Validated
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationsService;

    @GetMapping("/user/{userId}")
    public List<NotificationResponseDto> getUserNotifications(@PathVariable Long userId) {
        return notificationsService.getAllNotificationsForUser(userId);
    }
}