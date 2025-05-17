package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.dto.notification.UpdateNotificationStatusRequestDto;
import greencity.dto.user.UserVO;
import greencity.dto.notification.NotificationResponseDto;
import greencity.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import greencity.annotations.CurrentUserId;
import greencity.constant.HttpStatuses;
import greencity.dto.notification.BaseNotificationResponseDto;
import greencity.dto.notification.NotificationsGroupedResponseDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import greencity.enums.NotificationOrigin;
import java.util.Set;
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

    /**
     * Updates the status of a notification for the current user.
     *
     * <p>The notification must exist and belong to the current user. If the notification is not found,
     * a 404 response is returned. If the notification does not belong to the user, a 400 response is returned.
     * The status must be a valid value from the NotificationStatus enum.</p>
     *
     * @param request the request body containing the notification ID and the new status
     * @param user    the currently authenticated user
     * @return HTTP 200 OK if the status was successfully updated
     */
    @PutMapping("/status")
    @Operation(
        summary = "Update notification status",
        description = "Updates the status of a specific notification by its ID. "
            + "The notification must belong to the current user."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification status updated successfully"),
        @ApiResponse(responseCode = "400",
            description = "Invalid request data or the notification does not belong to the user"),
        @ApiResponse(responseCode = "401", description = "Unauthorized request"),
        @ApiResponse(responseCode = "404", description = "Notification not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> updateNotificationStatus(
        @RequestBody @Valid UpdateNotificationStatusRequestDto request,
        @Parameter(hidden = true) @CurrentUser UserVO user
    ) {
        notificationsService.updateNotificationStatus(request, user);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves all notifications for a specified user.
     * If the origin is null, notifications from all origins will be returned.
     *
     * @param userId the ID of the user
//     * @param origin the origin of the notifications (optional)
     * @return a {@link ResponseEntity} containing a {@link Set} of {@link NotificationResponseDto}
     */
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
            .body(notificationsService.getAllNotificationsForUser(userId, NotificationOrigin.GREEN_CITY));
    }
}