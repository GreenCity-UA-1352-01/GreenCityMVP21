package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.CurrentUserId;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationResponseDto;
import greencity.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "Find all notifications by user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @ApiPageable
    @GetMapping("/user/{userId}")
    public ResponseEntity<PageableDto<NotificationResponseDto>> getUserNotifications(
            @PathVariable @CurrentUserId Long userId,
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(notificationsService.getAllNotificationsForUser(userId, pageable));
    }
}