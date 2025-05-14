package greencity.controller;

import greencity.annotations.CurrentUserId;
import greencity.constant.HttpStatuses;
import greencity.dto.notification.BaseNotificationResponseDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.dto.notification.NotificationsGroupedResponseDto;
import greencity.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationsService;

    @Operation(summary = "Find all notifications by user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK, content = @Content(
                array = @ArraySchema(schema = @Schema(oneOf = {
                    NotificationResponseDto.class,
                    NotificationsGroupedResponseDto.class
                }))
            )),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content)
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<Set<BaseNotificationResponseDto>> getUserNotifications(
        @PathVariable @CurrentUserId Long userId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(notificationsService.getAllNotificationsForUser(userId));
    }
}