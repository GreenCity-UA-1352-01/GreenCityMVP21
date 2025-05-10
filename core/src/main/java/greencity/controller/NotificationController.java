package greencity.controller;

import greencity.annotations.ApiPageableWithoutSort;
import greencity.annotations.CurrentUserId;
import greencity.constant.ErrorMessage;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.enums.NotificationOrigin;
import greencity.exception.exceptions.InvalidOriginException;
import greencity.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
     * Retrieves all notifications for a specified user.
     * If the origin is null, notifications from all origins will be returned.
     *
     * @param userId   the ID of the user
     * @param origin   the origin of the notifications (optional)
     * @param pageable the pagination information
     * @return a {@link ResponseEntity} containing a {@link PageableDto} of {@link NotificationResponseDto}
     */
    @Operation(summary = "Find all notifications by user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN, content = @Content)
    })
    @ApiPageableWithoutSort
    @GetMapping("/user/{userId}")
    public ResponseEntity<PageableDto<NotificationResponseDto>> getUserNotifications(
            @PathVariable @CurrentUserId Long userId,
            @RequestParam(required = false) String origin,
            @Parameter(hidden = true) Pageable pageable) {
        NotificationOrigin originEnum = origin == null ? null : convertOrigin(origin);
        return ResponseEntity.status(HttpStatus.OK)
            .body(notificationsService.getAllNotificationsForUser(userId, originEnum, pageable));
    }

    private NotificationOrigin convertOrigin(String origin) {
        try {
            return NotificationOrigin.valueOf(origin);
        } catch (IllegalArgumentException e) {
            throw new InvalidOriginException(ErrorMessage.INVALID_ORIGIN + origin);
        }
    }
}